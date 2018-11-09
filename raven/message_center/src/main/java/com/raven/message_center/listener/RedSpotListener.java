package com.raven.message_center.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raven.message_center.utils.KafkaUtil;
import com.raven.message_center.consts.Const;
import com.raven.message_center.controller.RedSpotHandler;
import com.raven.message_center.bean.RedSpotReceiveBean;
import com.raven.message_center.service.RedSpotService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.util.Arrays;

/**
 * 接收红点提示
 */
@Component
public class RedSpotListener implements Runnable {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(RedSpotListener.class);

    @Autowired
    KafkaUtil kafkaUtil;

    @Autowired
    RedSpotHandler redSpotHandler;

    @Autowired
    RedSpotService redSpotService;

    @Override
    public void run() {
        KafkaConsumer<String, String> consumer = kafkaUtil.getConsumer();
        consumer.subscribe(Arrays.asList(Const.RED_SPOT_NOTICE));
        while (!Thread.currentThread().isInterrupted()) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            if (!records.isEmpty()) {
                for (ConsumerRecord record : records) {
                    try {
                        RedSpotReceiveBean bean = mapper.readValue(record.value().toString(), RedSpotReceiveBean.class);
                        redSpotService.addRedSpotMsg(bean);
                        redSpotHandler.sendMessageToUser(bean.getUid(), new TextMessage("看到这条信息的少年，你很有发展"));
                    } catch (Exception e) {
                        log.error("dto什么鬼:{}", record.value().toString());
                    }
                }
                consumer.commitAsync();
            }
        }
    }
}
