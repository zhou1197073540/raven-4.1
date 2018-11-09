package com.raven.message_center.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raven.message_center.consts.Const;
import com.raven.message_center.controller.ZhuangbilityHandler;
import com.raven.message_center.utils.KafkaUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.util.Arrays;

@Component
public class ZhuangbilityListener implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ZhuangbilityListener.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    KafkaUtil kafkaUtil;

    @Autowired
    ZhuangbilityHandler zhuangbilityHandler;


    /**
     * todo 消息处理还不是异步的，异常处理不足
     */
    @Override
    public void run() {
        KafkaConsumer<String, String> consumer = kafkaUtil.getConsumer();
        consumer.subscribe(Arrays.asList(Const.TAKER));
        while (!Thread.currentThread().isInterrupted()) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            if (!records.isEmpty()) {
                for (ConsumerRecord record : records) {
                    zhuangbilityHandler.sendMessageToUsers(new TextMessage(record.value().toString()));
                }
                //如果处理时间过长会出报错
                consumer.commitAsync();
            }
        }
    }

}
