package kr.cseungjoo.chome_be.shared.adapter.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.cseungjoo.chome_be.shared.adapter.mqtt.message.HubMessage;
import kr.cseungjoo.chome_be.shared.port.out.MqttPublishPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqttPublishAdapter implements MqttPublishPort {

    private final MqttClient mqttClient;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(String topic, HubMessage hubMessage) {
        try {
            byte[] payload = objectMapper.writeValueAsBytes(hubMessage);
            MqttMessage mqttMessage = new MqttMessage(payload);
            mqttMessage.setQos(1);
            mqttClient.publish(topic, mqttMessage);
            log.info("MQTT 메시지 발행: topic={}, type={}, requestId={}",
                    topic, hubMessage.getType(), hubMessage.getRequestId());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("MQTT 메시지 직렬화 실패", e);
        } catch (MqttException e) {
            throw new IllegalStateException("MQTT 메시지 발행 실패: " + topic, e);
        }
    }
}
