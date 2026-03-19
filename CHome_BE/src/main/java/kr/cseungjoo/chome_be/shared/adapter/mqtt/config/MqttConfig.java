package kr.cseungjoo.chome_be.shared.adapter.mqtt.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(MqttProperties.class)
@RequiredArgsConstructor
public class MqttConfig {

    private final MqttProperties mqttProperties;

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(60);

        if (mqttProperties.getUsername() != null) {
            options.setUserName(mqttProperties.getUsername());
        }
        if (mqttProperties.getPassword() != null) {
            options.setPassword(mqttProperties.getPassword().toCharArray());
        }

        return options;
    }

    @Bean
    public MqttClient mqttClient(MqttConnectOptions mqttConnectOptions) throws MqttException {
        MqttClient client = new MqttClient(
                mqttProperties.getBrokerUrl(),
                mqttProperties.getClientId(),
                new MemoryPersistence()
        );

        try {
            client.connect(mqttConnectOptions);
            log.info("MQTT 브로커 연결 성공: {}", mqttProperties.getBrokerUrl());
        } catch (MqttException e) {
            log.warn("MQTT 브로커 연결 실패, 자동 재연결 대기: {}", mqttProperties.getBrokerUrl());
            client.reconnect();
        }

        return client;
    }
}
