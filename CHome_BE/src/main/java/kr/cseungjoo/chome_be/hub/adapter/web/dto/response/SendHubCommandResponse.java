package kr.cseungjoo.chome_be.hub.adapter.web.dto.response;

public record SendHubCommandResponse(
        String requestId,
        String type
) {}
