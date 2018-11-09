package com.raven.utils;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Properties;

@Component
public class KafkaUtil {

    private static final Logger log = LoggerFactory.getLogger(KafkaUtil.class);

    @Autowired
    Properties kafkaProducerProps;

    @Autowired
    Properties kafkaConsumerProps;


    /**
     * 获取默认producer
     *
     * @return
     */
    public Producer getProducer() {
        return new KafkaProducer<>(kafkaProducerProps);
    }

    /**
     * 获取producer
     *
     * @param prop
     * @return
     */
    public Producer getProducer(Properties prop) {
        Producer<String, String> producer = new KafkaProducer<>(prop);
        return producer;
    }

    public void sendMessage(String topic, String message) {
        Producer<String, String> producer = getProducer();
        producer.send(new ProducerRecord(topic, message), new Callback() {

            //注：发送失败时metadata为null，有点怪
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception != null) {
                    log.error("kafka send message error:", exception);
                }
            }
        });
        producer.close();
    }

    /**
     * 获取默认consumer
     *
     * @return
     */
    public KafkaConsumer getConsumer() {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaConsumerProps);
        return consumer;
    }

    /**
     * 获取consumer
     *
     * @param props
     * @return
     */
    public KafkaConsumer getConsumer(Properties props) {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        return consumer;
    }

    /**
     * test
     *
     * @param args
     */
    public static void main(String[] args) {
        KafkaUtil kafkaUtil = new KafkaUtil();
        Properties props = new Properties();
        KafkaConsumer<String, String> consumer = kafkaUtil.getConsumer(props);
        consumer.subscribe(Arrays.asList("web"));
        int i = 0;
        while (true) {
            System.out.println(i);
            i++;
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.value() + "====" + i);
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e) {

            }
        }

    }

}
