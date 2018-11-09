package com.raven.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.raven.bean.SocketMessage;
import com.raven.common.MsgConstant;
import com.raven.controller.IndexValueHandler;
import com.raven.utils.KafkaUtil;
import com.raven.utils.TimeUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class KafkaListener implements CommandLineRunner {
    @Autowired
    KafkaUtil kafkaUtil;

    @Override
    public void run(String... strings) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    Properties props = KafkaUtil.getConsumerProperties(
//                            PropertiesUtil.getProperty("kafka_ip"), Constant.TRADE_GUIDE+"1");
//                    KafkaConsumer<String, String> consumer = KafkaUtil.getConsumer(props);
//                    consumer.subscribe(Arrays.asList(Constant.KUAIXUN_WEB));

                    KafkaConsumer<String, String> consumer = kafkaUtil.getConsumer();
                    consumer.subscribe(Arrays.asList("test_value"));
                    while (true) {
//                        System.out.println("===============");
                        try {
                            ConsumerRecords<String, String> records = consumer.poll(500);
                            for (ConsumerRecord<String, String> record : records) {
                                System.out.println("get a message : " + record.value());
                                IndexValueHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(record.value())));
                            }
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String alterTime(String value) {
        try {
            JSONObject obj = JSONObject.parseObject(value);
            obj.put("time", TimeUtil.getDateTime());
            return obj.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private List<String> sortDatas(List<String> datas) {
        if (null != datas && datas.size() > 0) {
            return datas
                    .stream()
                    .map(x -> JSONObject.parseObject(x))
                    .filter(x -> x.containsKey("time"))
                    .sorted((x, y) -> y.getString("time").compareTo(
                            x.getString("time")))
                    .map(x -> JSONObject.toJSONString(x))
                    .collect(Collectors.toList());
        }
        return null;
    }
}
