package com.leo.kafka.samples;

import java.util.Date;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloKafka {
	/**
	 * LOG
	 */
	private static Logger logger = LoggerFactory.getLogger(HelloKafka.class);
	/**
	 * <a href="http://stackoverflow.com/questions/23903843/apache-kafka-example-error-failed-to-send-message-after-3-tries">
	 * kafka.common.FailedToSendMessageException: Failed to send messages after 3 tries.
	 * </a>
	 */
	public static void main(String[] args) {
		Properties properties = new Properties();
		properties.put("metadata.broker.list", "localhost:9092,localhost:9093,localhost:9094");
		properties.put("serializer.class",StringEncoder.class.getName());
		properties.put("request.required.acks", "1");
		ProducerConfig config = new ProducerConfig(properties);
		Producer<String, String> producer = new Producer<String, String>(config);
		while(true){
			String runtime = new Date().toString();
			String msg = "Message Publishing Time - " + runtime;
			logger.info("print message :{}",msg);
			KeyedMessage<String, String> data = new KeyedMessage<String, String>("hello-kafka", msg);
			producer.send(data);
		}
	}
}
