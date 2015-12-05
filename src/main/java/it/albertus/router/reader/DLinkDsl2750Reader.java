package it.albertus.router.reader;

import it.albertus.router.engine.RouterData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

public class DLinkDsl2750Reader extends Reader {

	private interface Defaults {
		String COMMAND_INFO_ADSL_STATUS = "adsl show info";
		String COMMAND_INFO_ADSL_SNR = "adsl show info";
	}

	private static final String DEVICE_MODEL = "D-Link DSL-2750B";
	private static final String COMMAND_PROMPT = ">>";
	private static final String LOGIN_PROMPT = ":";

	@Override
	public boolean login() throws IOException {
		// Username...
		out.print(readFromTelnet(LOGIN_PROMPT, true).trim(), true);
		writeToTelnet(configuration.getString("router.username"));

		// Password...
		out.print(readFromTelnet(LOGIN_PROMPT, true).trim());
		writeToTelnet(configuration.getString("router.password"));

		// Welcome! (salto caratteri speciali (clear screen, ecc.)...
		readFromTelnet(COMMAND_PROMPT, true);
		return true;
	}

	@Override
	public RouterData readInfo() throws IOException {
		// Informazioni sulla portante ADSL...
		writeToTelnet(configuration.getString("dlink.2750.command.info.adsl.", Defaults.COMMAND_INFO_ADSL));
		readFromTelnet("{", true); // Avanzamento del reader fino all'inizio dei dati di interesse.
		final Map<String, String> info = new LinkedHashMap<String, String>();
		BufferedReader reader = new BufferedReader(new StringReader(readFromTelnet("}", false).trim()));
		String line;
		while ((line = reader.readLine()) != null) {
			info.put(line.substring(0, line.indexOf('=')).trim(), line.substring(line.indexOf('=') + 1).trim());
		}
		reader.close();
		readFromTelnet(COMMAND_PROMPT, true); // Avanzamento del reader fino al prompt dei comandi.

		// Informazioni sulla connessione ad Internet...
		final String commandInfoWan = configuration.getString("tplink.8970.command.info.wan");
		if (commandInfoWan != null && commandInfoWan.trim().length() != 0) {
			writeToTelnet(commandInfoWan);
			readFromTelnet("{", true);
			reader = new BufferedReader(new StringReader(readFromTelnet("}", false).trim()));
			while ((line = reader.readLine()) != null) {
				info.put(line.substring(0, line.indexOf('=')).trim(), line.substring(line.indexOf('=') + 1).trim());
			}
			reader.close();
			readFromTelnet(COMMAND_PROMPT, true);
		}

		return new RouterData(info);
	}

	@Override
	public String getDeviceModel() {
		return DEVICE_MODEL;
	}

}
