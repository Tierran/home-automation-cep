package com.ninetailsoftware.mqtt.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttClientWrapper {

	private Logger log = LoggerFactory.getLogger(MqttClient.class);

	private String topic = "/brms/";
	private String content = "Message from MqttPublishSample";
	private int qos = 0;
	private String broker;
	private String clientId = "brms";
	private MemoryPersistence persistence = new MemoryPersistence();

	private static MqttClient mqttClient;

	public MqttClientWrapper() {
		if (mqttClient == null) {
			this.createClient();
		}
	}

	public void sendMessage(String message) {

		try {
			log.info("Publishing message: " + content);
			MqttMessage mqttMessage = new MqttMessage(message.getBytes());
			mqttMessage.setQos(qos);
			mqttClient.publish(topic, mqttMessage);
			log.info("Message published");
		} catch (MqttPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			log.error(e.getMessage());
			this.createClient();
			this.sendMessage(message);
		}
	}

	private void createClient() {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("home-automation-cep.properties");
		Properties appProps = new Properties();
		
		try {
			appProps.load(is);
			
			broker = appProps.getProperty("broker");
			mqttClient = new MqttClient(broker, clientId, persistence);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			connOpts.setUserName(appProps.getProperty("mosquitto_user"));
			connOpts.setPassword(appProps.getProperty("mosquitto_password").toCharArray());
			log.info("Connecting to broker: " + broker);
			mqttClient.connect(connOpts);
			log.info("Connected");

		} catch (MqttException me) {
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
