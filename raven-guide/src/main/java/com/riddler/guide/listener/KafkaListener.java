package com.riddler.guide.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.riddler.guide.bean.SocketMessage;
import com.riddler.guide.common.Constant;
import com.riddler.guide.socket.MyWebSocketHandler;
import com.riddler.guide.util.KafkaUtil;
import com.riddler.guide.util.TimeUtil;
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
import java.util.Properties;
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
                    consumer.subscribe(Arrays.asList(Constant.KUAIXUN_WEB));
                    while (true) {
                        System.out.println("===============");
                        try {
                            ConsumerRecords<String, String> records = consumer.poll(500);
                            List<String> data = new ArrayList<String>();
                            for (ConsumerRecord<String, String> record : records) {
                                data.add(alterTime(record.value()));
                                System.out.println("get a message : " + record.value());
                            }
                            if (data.size() > 0) {
                                SocketMessage msg = new SocketMessage();
                                msg.setType(Constant.KAFKA_MSG_TYPE);
                                msg.setData(sortDatas(data));
//                                msg.setData(data);
                                MyWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(msg)));
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
