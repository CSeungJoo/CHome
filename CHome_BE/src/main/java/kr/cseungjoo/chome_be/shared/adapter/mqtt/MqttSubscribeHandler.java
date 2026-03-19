package kr.cseungjoo.chome_be.shared.adapter.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.cseungjoo.chome_be.hub.port.out.HubRepositoryPort;
import kr.cseungjoo.chome_be.shared.adapter.mqtt.message.HubMessage;
import kr.cseungjoo.chome_be.shared.adapter.mqtt.message.MessageKind;
import kr.cseungjoo.chome_be.shared.port.out.SseEmitterPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqttSubscribeHandler implements IMqttMessageListener {

    private static final String RESULT_TOPIC = "hub/+/result";
    private static final String EVENT_TOPIC = "hub/+/event";

    private final MqttClient mqttClient;
    private final ObjectMapper objectMapper;
    private final HubRepositoryPort hubRepositoryPort;
    private final SseEmitterPort sseEmitterPort;

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
        sendToHub(serialNumber, "result", message);
    }

    private void handleEvent(String serialNumber, HubMessage message) {
        log.info("EVENT 수신: hub={}, type={}, timestamp={}",
                serialNumber, message.getType(), message.getTimestamp());
        sendToHub(serialNumber, "event", message);
    }

    private void sendToHub(String serialNumber, String eventName, HubMessage message) {
        hubRepositoryPort.findBySerialNumber(serialNumber).ifPresentOrElse(
                hub -> {
                    Map<String, Object> sseData = Map.of(
                            "hubSerialNumber", serialNumber,
                            "kind", message.getKind(),
                            "type", message.getType(),
                            "requestId", message.getRequestId() != null ? message.getRequestId() : "",
                            "timestamp", message.getTimestamp(),
                            "payload", message.getPayload() != null ? message.getPayload() : Map.of()
                    );
                    sseEmitterPort.sendToHub(hub.getId(), eventName, sseData);
                },
                () -> log.warn("허브를 찾을 수 없음: serialNumber={}", serialNumber)
        );
    }

    private String extractSerialNumber(String topic) {
        String[] parts = topic.split("/");
        return parts[1];
    }
}
