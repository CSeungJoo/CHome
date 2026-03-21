package kr.cseungjoo.chome_be.auth.port.in;

public record CheckMqttAclCommand(
        String username,
        String topic,
        int acc
) {}
