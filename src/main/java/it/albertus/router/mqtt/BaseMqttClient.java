package it.albertus.router.mqtt;

import java.io.UnsupportedEncodingException;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public abstract class BaseMqttClient {

	public static final String PREFERRED_CHARSET = "UTF-8";

	private volatile MqttClient client;

	protected class MqttClientStartThread extends Thread {

		private final MqttConnectOptions options;

		protected MqttClientStartThread(final MqttConnectOptions options) {
			this.setName("mqttClientStartThread");
			this.setDaemon(true);
			this.options = options;
		}

		@Override
		public void run() {
			try {
				client.connect(options);
			}
			catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected abstract void connect();

	public void disconnect() {
		try {
			doDisconnect();
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}

	protected synchronized void doConnect(final String clientId, final MqttConnectOptions options) throws MqttException {
		if (client == null) {
			client = new MqttClient(options.getServerURIs()[0], clientId);
			client.setCallback(new MqttCallback(clientId));
			final Thread starter = new MqttClientStartThread(options);
			starter.start();
			try {
				starter.join();
			}
			catch (final InterruptedException ie) {/* Ignore */}
		}
	}

	protected synchronized void doPublish(final String topic, final MqttMessage message) throws MqttException {
		if (client == null) {
			connect(); // Lazy connection.
		}
		if (client != null && client.isConnected()) {
			client.publish(topic, message);
		}
	}

	protected synchronized void doDisconnect() throws MqttException {
		if (client != null) {
			if (client.isConnected()) {
				try {
					client.disconnect();
				}
				catch (final Exception e) {
					e.printStackTrace();
					client.disconnectForcibly();
				}
			}
			client.close();
			client = null;
		}
	}

	protected byte[] createPayload(final Object object) {
		return createPayload(object != null ? object.toString() : null);
	}

	protected byte[] createPayload(final String string) {
		byte[] payload;
		if (string != null) {
			try {
				payload = string.getBytes(PREFERRED_CHARSET);
			}
			catch (final UnsupportedEncodingException uee) {
				payload = string.getBytes();
			}
		}
		else {
			payload = "".getBytes();
		}
		return payload;
	}

}