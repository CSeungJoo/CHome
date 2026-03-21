package kr.cseungjoo.chome_be.auth.adapter.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MqttAclRequest(
        @NotBlank String username,
        @NotBlank String topic,
        String clientid,
        int acc
) {}
