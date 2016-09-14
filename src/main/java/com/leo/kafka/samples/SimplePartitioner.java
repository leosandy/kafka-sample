package com.leo.kafka.samples;

import kafka.producer.Partitioner;

public class SimplePartitioner implements Partitioner {

	public SimplePartitioner(kafka.utils.VerifiableProperties prop){
		System.out.println("props:"+prop.toString());
	}
	
	public int partition(Object key, int partitioners) {
		int partition = 0;
		String partitionKey = (String) key;
		System.out.printf("key:%s,partitioners:%d",partitionKey,partitioners);
		int offset = partitionKey.lastIndexOf(".");
		
		if(offset > 0){
			partition = Integer.parseInt(partitionKey.substring(offset + 1)) % partitioners;
			System.out.println("mode:"+partition);
		}
		return partition;
	}

}
