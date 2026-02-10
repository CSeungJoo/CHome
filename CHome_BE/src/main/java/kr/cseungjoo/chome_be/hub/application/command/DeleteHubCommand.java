package kr.cseungjoo.chome_be.hub.application.command;

public record DeleteHubCommand(
        long userId,
        long hubId
) {}