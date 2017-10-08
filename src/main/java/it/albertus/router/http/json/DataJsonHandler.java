package it.albertus.router.http.json;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import it.albertus.httpserver.annotation.Path;
import it.albertus.httpserver.config.IHttpServerConfig;
import it.albertus.router.dto.RouterDataDto;
import it.albertus.router.engine.RouterLoggerEngine;

@Path("/json/data")
public class DataJsonHandler extends AbstractJsonHandler {

	public DataJsonHandler(final IHttpServerConfig config, final RouterLoggerEngine engine) {
		super(config, engine);
	}

	@Override
	protected void doGet(final HttpExchange exchange) throws IOException {
		final byte[] payload = Payload.createPayload(new RouterDataDto(engine.getCurrentData()).toJson());
		addRefreshHeader(exchange);
		sendResponse(exchange, payload);
	}

}
