package kr.cseungjoo.chome_be.shared.adapter.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.cseungjoo.chome_be.shared.adapter.mqtt.message.HubMessage;
import kr.cseungjoo.chome_be.shared.adapter.mqtt.message.MessageKind;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqttSubscribeHandler implements IMqttMessageListener {

    private static final String RESULT_TOPIC = "hub/+/result";
    private static final String EVENT_TOPIC = "hub/+/event";

    private final MqttClient mqttClient;
    private final ObjectMapper objectMapper;

    @EventListener(ApplicationReadyEvent.class)
    public void subscribe() throws MqttException {
        mqttClient.subscribe(RESULT_TOPIC, 1, this);
        mqttClient.subscribe(EVENT_TOPIC, 1, this);
        log.info("MQTT 구독 시작: {}, {}", RESULT_TOPIC, EVENT_TOPIC);
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        try {
            HubMessage message = objectMapper.readValue(mqttMessage.getPayload(), HubMessage.class);
            String serialNumber = extractSerialNumber(topic);

            if (message.getKind() == MessageKind.RESULT) {
                handleResult(serialNumber, message);
            } else if (message.getKind() == MessageKind.EVENT) {
                handleEvent(serialNumber, message);
            }
        } catch (Exception e) {
            log.error("MQTT 메시지 처리 실패: topic={}", topic, e);
        }
    }

    private void handleResult(String serialNumber, HubMessage message) {
        log.info("RESULT 수신: hub={}, type={}, requestId={}",
                serialNumber, message.getType(), message.getRequestId());
    }

    private void handleEvent(String serialNumber, HubMessage message) {
        log.info("EVENT 수신: hub={}, type={}, timestamp={}",
                serialNumber, message.getType(), message.getTimestamp());
    }

    private String extractSerialNumber(String topic) {
        // topic format: hub/{serialNumber}/result or hub/{serialNumber}/event
        String[] parts = topic.split("/");
        return parts[1];
    }
}
