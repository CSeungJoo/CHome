package kr.cseungjoo.chome_be.auth.adapter.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MqttAuthRequest(
        String clientId,
        @NotBlank String username,
        String password
) {}
