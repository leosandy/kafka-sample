package com.leo.kafka.samples;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class SimpleHLConsumer {

	private ExecutorService executorService;
	
	private ConsumerConnector consumerConnector;
	
	private final String topic;
	
	
	public SimpleHLConsumer(String zookeeper,String groupId,String topic){
		this.topic = topic;
		consumerConnector = Consumer.createJavaConsumerConnector(createConsumerConfig(zookeeper, groupId));
		
	}
	
	private static ConsumerConfig createConsumerConfig(String zookeeper,String groupId){
		Properties prop = new Properties();
		prop.put("zookeeper.connect", zookeeper);
		prop.put("group.id", groupId);
		prop.put("zookeeper.session.timeout.ms", "5000");
		prop.put("zookeeper.sync.time.ms", "250");
		prop.put("auto.commit.interval.ms","1000");
		return new ConsumerConfig(prop);
	}
	
	public void testComsumer(){
		Map<String, Integer> topicMap = new HashMap<String, Integer>();
		topicMap.put(topic, new Integer(1));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreamMap = consumerConnector.createMessageStreams(topicMap);
		while (true) {
			List<KafkaStream<byte[], byte[]>> streamList = consumerStreamMap.get(topic);
			for (KafkaStream<byte[], byte[]> kafkaStream : streamList) {
				ConsumerIterator<byte[], byte[]> consumerIterator = kafkaStream.iterator();
				while (consumerIterator.hasNext()) {
					System.out.println("Message from single topic :: " + new String(consumerIterator.next().message()));
				}
			}
		}
	}
	
	public void testMultiThreadConsumer(int threadCount){
		Map<String, Integer> topicMap = new HashMap<String, Integer>();
		topicMap.put(topic, threadCount);
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreamMap = consumerConnector.createMessageStreams(topicMap);
		executorService = Executors.newFixedThreadPool(threadCount);
		
		while (true) {
			int count = 0;
			List<KafkaStream<byte[], byte[]>> streamList = consumerStreamMap.get(topic);
			for (final KafkaStream<byte[], byte[]> kafkaStream : streamList) {
				final int threadNumber = count;
				executorService.execute(new Runnable() {
					
					public void run() {
						ConsumerIterator<byte[], byte[]> consumerIterator = kafkaStream.iterator();
						while (consumerIterator.hasNext()) {
							System.out.println("Thread Number " + threadNumber +" : " + new String(consumerIterator.next().message()));
							System.out.println("Shutting	down	Thread	Number:	"	+	
									threadNumber);
						} 
					}
				});
				count ++;
			}
		}
	
	}
	
	public void shutdown(){
		if(consumerConnector != null){
			consumerConnector.shutdown();
		}
	}
	
	public static void main(String[] args) {
		new SimpleHLConsumer("localhost:2181", "website-hits", "website-hits").testMultiThreadConsumer(3);
	}
	
	
}
