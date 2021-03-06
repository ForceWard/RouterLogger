package it.albertus.routerlogger.http.html;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;

import it.albertus.net.httpserver.HttpException;
import it.albertus.net.httpserver.HttpMethod;
import it.albertus.net.httpserver.annotation.Path;
import it.albertus.net.httpserver.config.IHttpServerConfig;
import it.albertus.net.httpserver.html.HtmlUtils;
import it.albertus.routerlogger.resources.Messages;
import it.albertus.util.IOUtils;
import it.albertus.util.NewLine;
import it.albertus.util.PropertiesComparator;
import it.albertus.util.PropertiesComparator.CompareResults;
import it.albertus.util.StringUtils;
import it.albertus.util.logging.LoggerFactory;

@Path("/configuration")
public class ConfigurationHandler extends AbstractHtmlHandler {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationHandler.class);

	public static class Defaults {
		public static final boolean ENABLED = false;

		private Defaults() {
			throw new IllegalAccessError("Constants class");
		}
	}

	static final String CFG_KEY_ENABLED = "server.handler.configuration.enabled";

	private static final String REQUEST_PARAM_NAME = "properties";

	public ConfigurationHandler(final IHttpServerConfig config) {
		super(config);
	}

	@Override
	protected void doGet(final HttpExchange exchange) throws IOException {
		final StringBuilder html = new StringBuilder(buildHtmlHeader(Messages.get("lbl.server.configuration")));

		html.append("<div class=\"page-header\">").append(NewLine.CRLF);
		html.append("<h2>").append(HtmlUtils.escapeHtml(Messages.get("lbl.server.configuration"))).append(buildHtmlRefreshButton()).append("</h2>").append(NewLine.CRLF);
		html.append("</div>").append(NewLine.CRLF); // page-header

		// Form
		html.append("<form action=\"").append(getPath()).append("\" method=\"").append(HttpMethod.POST).append("\"><div class=\"form-group\">");
		html.append("<textarea class=\"form-control textarea-properties\" rows=\"14\" cols=\"80\" name=\"").append(REQUEST_PARAM_NAME).append("\">").append(HtmlUtils.escapeHtml(getPropertiesAsString(configuration.getProperties()))).append("</textarea>").append(NewLine.CRLF);
		html.append("</div>").append(NewLine.CRLF);
		html.append("<div class=\"form-group\">").append(NewLine.CRLF);
		html.append("<input class=\"btn btn-danger btn-md pull-right btn-bottom\" type=\"submit\" value=\"").append(HtmlUtils.escapeHtml(Messages.get("lbl.server.save"))).append("\" onclick=\"return confirm('").append(HtmlUtils.escapeEcmaScript(Messages.get("msg.server.configuration.confirm.save"))).append("');\" />").append(NewLine.CRLF);
		html.append("</div></form>").append(NewLine.CRLF);

		html.append(buildHtmlFooter());

		sendResponse(exchange, html.toString());

		logger.log(Level.WARNING, Messages.get("msg.server.configuration.open"), new Object[] { Thread.currentThread().getName(), exchange.getRemoteAddress(), exchange.getRequestMethod(), exchange.getRequestURI() });
	}

	@Override
	protected void doPut(final HttpExchange exchange) throws IOException {
		doPost(exchange);
	}

	@Override
	protected void doPost(final HttpExchange exchange) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(exchange.getRequestBody(), baos, 1024);
		final String requestBody = baos.toString(getCharset().name());
		final String prefix = REQUEST_PARAM_NAME + '=';
		if (!requestBody.startsWith(prefix)) {
			throw new HttpException(HttpURLConnection.HTTP_BAD_REQUEST, Messages.get("msg.server.bad.request"));
		}
		final Properties updatedProperties = new Properties();
		try {
			updatedProperties.load(new StringReader(HtmlUtils.unescapeHtml(URLDecoder.decode(StringUtils.substringAfter(requestBody, prefix), getCharset().name()))));
		}
		catch (final IOException e) {
			throw new HttpException(HttpURLConnection.HTTP_BAD_REQUEST, Messages.get("msg.server.bad.request"), e);
		}
		if (!updatedProperties.equals(configuration.getProperties())) {
			final Properties backup = new Properties();
			backup.putAll(configuration.getProperties());
			try {
				configuration.save(updatedProperties);
				configuration.reload();
				final CompareResults results = PropertiesComparator.compare(backup, configuration.getProperties());
				logger.log(Level.WARNING, Messages.get("msg.server.configuration.save"), new Object[] { Thread.currentThread().getName(), exchange.getRemoteAddress(), exchange.getRequestMethod(), exchange.getRequestURI(), results.getRightOnly(), results.getLeftOnly(), results.getDifferentValues() });
			}
			catch (final IOException e) {
				throw new HttpException(HttpURLConnection.HTTP_INTERNAL_ERROR, Messages.get("err.server.handler"), e);
			}
		}

		// Post/Redirect/Get
		exchange.getResponseHeaders().set("Location", getPath());
		exchange.sendResponseHeaders(HttpURLConnection.HTTP_SEE_OTHER, -1);
		exchange.getResponseBody().close(); // Needed when no write occurs.
		exchange.close();
	}

	@Override
	public boolean isEnabled() {
		return configuration.getBoolean(CFG_KEY_ENABLED, Defaults.ENABLED);
	}

	private static String getPropertiesAsString(final Properties properties) {
		final StringBuilder sb = new StringBuilder();
		try {
			final StringWriter writer = new StringWriter();
			properties.store(writer, null);
			for (final String line : getSortedLines(new BufferedReader(new StringReader(writer.toString())))) {
				sb.append(line).append(NewLine.SYSTEM_LINE_SEPARATOR);
			}
		}
		catch (final IOException e) {
			throw new IllegalStateException(e); // StringWriter cannot throw IOException
		}
		return sb.toString();
	}

	private static Set<String> getSortedLines(final BufferedReader reader) throws IOException {
		final Set<String> lines = new TreeSet<String>();
		String line;
		while ((line = reader.readLine()) != null) {
			if (!line.startsWith("#")) { // discard comments
				lines.add(line);
			}
		}
		return lines;
	}

}
