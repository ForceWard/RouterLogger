package it.albertus.router.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import it.albertus.router.server.annotation.Path;
import it.albertus.util.IOUtils;
import it.albertus.util.logging.LoggerFactory;

@Path('/' + FaviconHandler.RESOURCE_NAME)
public class FaviconHandler extends StaticResourceHandler {

	private static final Logger logger = LoggerFactory.getLogger(FaviconHandler.class);

	protected static final String RESOURCE_NAME = "favicon.ico";

	private static final byte[] favicon = loadFavicon(); // Cached

	public FaviconHandler() {
		super(RESOURCE_NAME, createHeaders());
		setFound(favicon != null);
	}

	private static byte[] loadFavicon() {
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try (final InputStream inputStream = FaviconHandler.class.getResourceAsStream(RESOURCE_NAME)) {
			IOUtils.copy(inputStream, outputStream, BUFFER_SIZE);
		}
		catch (final IOException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
		return outputStream.toByteArray();
	}

	private static Headers createHeaders() {
		final Headers faviconHeaders = new Headers();
		faviconHeaders.add("Content-Type", "image/x-icon");
		faviconHeaders.add("Cache-Control", "no-transform,public,max-age=86400,s-maxage=259200");
		return faviconHeaders;
	}

	@Override
	protected void doGet(final HttpExchange exchange) throws IOException {
		sendResponse(exchange, favicon);
	}

}
