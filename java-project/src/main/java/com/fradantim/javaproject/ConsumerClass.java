package com.fradantim.javaproject;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class ConsumerClass {
	
	public static final String BOOTSTRAP_SERVERS = "localhost:9092";
	public static final String TOPIC_NAME = "first_topic";
	
	public static final String CONSUMER_GROUP_ID = "my-great-app";
	
	public static final Duration TIMEOUT = Duration.ofMillis(100);
	
    public static void main(String... args) {
    	try {
    		consume();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    
    public static void consume() {
    	// create consumer properties
    	Properties properties = new Properties();
    	properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    	properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    	properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    	properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_ID);
    	properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    	
    	//create consumer
    	KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
    	
    	// subscribe consumer to out topics()
    	consumer.subscribe(Arrays.asList(TOPIC_NAME));
    	
    	// poll for new data
    	while (true) {
    		ConsumerRecords<String, String> records = consumer.poll(TIMEOUT);
    		System.out.println("PULLED!");
    		for(ConsumerRecord<String, String> record: records) {
    			System.out.println("- - - -");
    			System.out.println("\tKey: " + record.key());
    			System.out.println("\tValue: " + record.value());
    			System.out.println("\tParttition: " + record.partition());
    			System.out.println("\tOffset: " + record.offset());
    		}
    	}
    }
    
    public static void consumeWithGroups() {
    	// create consumer properties
    	Properties properties = new Properties();
    	properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    	properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    	properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    	properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_ID);
    	properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    	
    	//create consumer
    	KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
    }
 
}
