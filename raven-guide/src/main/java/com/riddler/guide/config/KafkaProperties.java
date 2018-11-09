package com.riddler.guide.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

@Component
public class KafkaProperties {

    @Autowired
    Environment env;

    @Bean
    public Properties kafkaProducerProps() {
        Properties props = new Properties();
        Map<String, Object> envMap = getAllEnvProps();
        for (String key : envMap.keySet()) {
            if (key.startsWith("producer")) {
                Object value = envMap.get(key);
                props.put(key.replaceFirst("producer.", ""), value);
            }
        }
        return props;
    }

    @Bean
    public Properties kafkaConsumerProps() {
        Properties props = new Properties();
        Map<String, Object> envMap = getAllEnvProps();
        for (String key : envMap.keySet()) {
            if (key.startsWith("consumer")) {
                Object value = envMap.get(key);
                props.put(key.replaceFirst("consumer.", ""), value);
            }
        }
        return props;
    }

    private Map<String, Object> getAllEnvProps() {
        Map<String, Object> map = new HashMap();
        for (Iterator it = ((AbstractEnvironment) env).getPropertySources().iterator(); it.hasNext(); ) {
            PropertySource propertySource = (PropertySource) it.next();
            String fileName = propertySource.getName();
            if (propertySource instanceof MapPropertySource&&
                    fileName.contains("kafka")) {
                map.putAll(((MapPropertySource) propertySource).getSource());
            }
        }
        return map;
    }
}
