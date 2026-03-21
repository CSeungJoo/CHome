package kr.cseungjoo.chome_be.shared.adapter.mqtt;

import org.springframework.context.ApplicationEvent;

public class MqttConnectedEvent extends ApplicationEvent {
    public MqttConnectedEvent(Object source) {
        super(source);
    }
}
