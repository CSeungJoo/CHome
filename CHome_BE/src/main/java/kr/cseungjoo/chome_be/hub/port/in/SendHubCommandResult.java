package kr.cseungjoo.chome_be.hub.port.in;

public record SendHubCommandResult(
        String requestId,
        String type
) {}
