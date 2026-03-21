package kr.cseungjoo.chome_be.hub.adapter.web.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record SendHubCommandRequest(
        @NotBlank String type,
        Map<String, Object> payload
) {}
