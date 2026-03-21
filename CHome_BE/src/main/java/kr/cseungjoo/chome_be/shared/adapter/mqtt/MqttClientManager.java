package kr.cseungjoo.chome_be.shared.adapter.mqtt;

import kr.cseungjoo.chome_be.shared.adapter.mqtt.config.MqttProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqttClientManager {

    private final MqttProperties mqttProperties;
    private final MqttConnectOptions mqttConnectOptions;
    private final ApplicationEventPublisher eventPublisher;

    private MqttClient client;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            client = new MqttClient(
                    mqttProperties.getBrokerUrl(),
                    mqttProperties.getClientId(),
                    new MemoryPersistence()
            );
        } catch (MqttException e) {
            log.error("MQTT 클라이언트 생성 실패", e);
            return;
        }

        connect();
    }

    public MqttClient getClient() {
        return client;
    }

    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    private void connect() {
        Thread.ofVirtual().start(() -> {
            while (!isConnected()) {
                try {
                    log.info("MQTT 브로커 연결 시도: {}", mqttProperties.getBrokerUrl());
                    client.connect(mqttConnectOptions);
                    log.info("MQTT 브로커 연결 성공: {}", mqttProperties.getBrokerUrl());
                    eventPublisher.publishEvent(new MqttConnectedEvent(this));
                } catch (MqttException e) {
                    log.warn("MQTT 브로커 연결 실패, 5초 후 재시도");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        });
    }
}
