package com.ninetailsoftware.kie.engine.listener;

import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ninetailsoftware.model.events.HaEvent;
import com.ninetailsoftware.model.facts.Device;
import com.ninetailsoftware.mqtt.client.MqttClientWrapper;

public class FactUpdateListener implements RuleRuntimeEventListener {

	Logger log = LoggerFactory.getLogger(FactUpdateListener.class);

	public void objectInserted(ObjectInsertedEvent event) {
		log.info("Detected Object Inserted Event : " + event.getObject().toString());

		if (event.getObject().toString().contains("com.ninetailsoftware.model.events.HaEvent")) {
			HaEvent haEvent = (HaEvent) event.getObject();
			log.info("Event Inserted: " + haEvent.getDeviceId());
		} else {
			Device device = (Device) event.getObject();
			log.info("Device Inserted: " + device.getId() + " : " + device.getStatus());

			if (device.getSource() != null && !device.getSource().equals("homeseer")) {
				MqttClientWrapper mqttClient = new MqttClientWrapper();
				String message = device.getId();
				mqttClient.sendMessage(message);
			} else if (device.isInitialLoad() != null && device.isInitialLoad()) {
				MqttClientWrapper mqttClient = new MqttClientWrapper();
				String message = device.getId();
				mqttClient.sendMessage(message, "/status");
			}
		}

	}

	public void objectUpdated(ObjectUpdatedEvent event) {
		log.info("Detected Object Update Event : " + event.getObject().toString());

		Device device = (Device) event.getObject();

		log.info("Device Updated: " + device.getId() + " : " + device.getStatus());

		if (device.getSource() != null && !device.getSource().equals("homeseer")) {
			MqttClientWrapper mqttClient = new MqttClientWrapper();
			String message = device.getId() + "," + device.getStatus();
			mqttClient.sendMessage(message);
		}
	}

	public void objectDeleted(ObjectDeletedEvent event) {
		log.info("Detected Object Deleted Event");
		if (event.getOldObject().toString().contains("com.ninetailsoftware.model.events.HaEvent")) {
			HaEvent haEvent = (HaEvent) event.getOldObject();
			log.info("Event Deleted: " + haEvent.getDeviceId());
		}
	}

}
