package com.fradantim.javaproject;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class ProducerClass {
	
	public static final String BOOTSTRAP_SERVERS = "localhost:9092";
	public static final String TOPIC_NAME = "first_topic";
	
    public static void main(String... args) {
    	try {
    	// simpleProduction();
    	// productionWithCallback();
    	productionWithKeys();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * Will commit on Producer close
     * */
    public static void simpleProduction() {
    	// create Producer properties
    	Properties properties = new Properties();
    	properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    	properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    	properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    	
    	// create the Producer
    	// .close() will flush the producer
    	try (KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);) {
    		// create Producer Record
        	ProducerRecord<String, String> record = new ProducerRecord<String, String>(TOPIC_NAME, "Hello there! "+new Date());

        	// send data -> async operation
        	producer.send(record);
        	
    	}
    }
    
    public static void productionWithCallback() {
    	
    	// create Producer properties
    	Properties properties = new Properties();
    	properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    	properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    	properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    	
    	// create the Producer
    	// .close() will flush the producer
    	try (KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);) {

    		for (int i=0; i<10; i++) {
    			// create Producer Record
            	ProducerRecord<String, String> record = new ProducerRecord<String, String>(TOPIC_NAME, "("+i+")\tHello there! "+new Date());

            	// send data -> async operation
            	producer.send(
        			record, 
        			(recordMetadata, exception) -> {
        				// executes each time a record is successfully sent or error ocurrs 
        				
        				if(exception == null) {
        					System.out.println("Message sent OK!");
        					System.out.println("\tTopic:"+recordMetadata.topic());
        					System.out.println("\tPartition:"+recordMetadata.partition());
        					System.out.println("\tOffset:"+recordMetadata.offset());
        					System.out.println("\tTimestamp:"+recordMetadata.timestamp());
        				} else {
        					System.out.println( "An error ocurred ):");
        					exception.printStackTrace();;
        				}
        			}
        		); // I can make it synhronous by adding .get()
    		}
    	}
    }
    
    public static void productionWithKeys() throws InterruptedException, ExecutionException {
    	// create Producer properties
    	Properties properties = new Properties();
    	properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    	properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    	properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    	
    	// create the Producer
    	// .close() will flush the producer
    	try (KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);) {

    		for (int i=0; i<10; i++) {
    			// create Producer Record
    			
    			String topic = TOPIC_NAME;
    			String value = "("+i+")\tHello there! "+new Date();
    			String key = "id_"+ i;
    			
            	ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, key, value);

            	System.out.println("Key to send: "+key);
            	
            	// send data -> async operation
            	producer.send(
        			record, 
        			(recordMetadata, exception) -> {
        				// executes each time a record is successfully sent or error ocurrs 
        				
        				if(exception == null) {
        					System.out.println("Message sent OK!");
        					System.out.println("\tTopic:"+recordMetadata.topic());
        					System.out.println("\tPartition:"+recordMetadata.partition());
        					System.out.println("\tOffset:"+recordMetadata.offset());
        					System.out.println("\tTimestamp:"+recordMetadata.timestamp());
        				} else {
        					System.out.println( "An error ocurred ):");
        					exception.printStackTrace();;
        				}
        			}
        		).get();  // I can make it synhronous by adding .get()
    		}
    	}
    }
}
