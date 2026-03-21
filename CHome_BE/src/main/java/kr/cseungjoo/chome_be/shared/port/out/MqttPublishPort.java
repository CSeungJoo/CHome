package kr.cseungjoo.chome_be.shared.port.out;

import kr.cseungjoo.chome_be.shared.adapter.mqtt.message.HubMessage;

public interface MqttPublishPort {
    void publish(String topic, HubMessage message);
}
