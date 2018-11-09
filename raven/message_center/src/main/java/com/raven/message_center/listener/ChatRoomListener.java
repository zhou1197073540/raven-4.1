package com.raven.message_center.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raven.message_center.bean.ChatRoomBean;
import com.raven.message_center.consts.Const;
import com.raven.message_center.controller.ChatRoomHandler;
import com.raven.message_center.utils.KafkaUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.util.Arrays;
import java.util.List;

@Component
public class ChatRoomListener implements Runnable {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    KafkaUtil kafkaUtil;

    @Autowired
    ChatRoomHandler chatRoomHandler;

    /**
     * todo 接收和消费还不是异步的，异常处理不足
     */
    @Override
    public void run() {
        KafkaConsumer<String, String> consumer = kafkaUtil.getConsumer();
        consumer.subscribe(Arrays.asList(Const.CHAT_ROOM));
        while (!Thread.currentThread().isInterrupted()) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            if (!records.isEmpty()) {
                for (ConsumerRecord record : records) {
                    chatRoomHandler.sendMessageToUsers(new TextMessage(record.value().toString()));
                }
                consumer.commitAsync();
            }
        }
    }
}
