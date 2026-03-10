package kr.cseungjoo.chome_be.hub.port.in;

public record DeleteHubCommand(
        long userId,
        long hubId
) {}