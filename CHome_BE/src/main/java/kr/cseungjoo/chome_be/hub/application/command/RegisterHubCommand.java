package kr.cseungjoo.chome_be.hub.application.command;

public record RegisterHubCommand(String serialNumber, String alias, long ownerId) {
}
