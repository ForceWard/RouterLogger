package it.albertus.routerlogger.http;

import java.util.logging.Level;

import it.albertus.net.httpserver.config.SingleUserAuthenticatorDefaultConfig;
import it.albertus.routerlogger.engine.RouterLoggerConfig;
import it.albertus.util.Configuration;

public class AuthenticatorConfig extends SingleUserAuthenticatorDefaultConfig {

	public static final String DEFAULT_FAILURE_LOGGING_LEVEL = Level.WARNING.getName();

	private final Configuration configuration = RouterLoggerConfig.getInstance();

	@Override
	public String getUsername() {
		return configuration.getString("server.username");
	}

	@Override
	public char[] getPassword() {
		return configuration.getCharArray("server.password");
	}

	@Override
	public String getRealm() {
		return "Restricted area";
	}

	@Override
	public String getFailureLoggingLevel() {
		return configuration.getString("server.log.auth.failed", DEFAULT_FAILURE_LOGGING_LEVEL);
	}

}
