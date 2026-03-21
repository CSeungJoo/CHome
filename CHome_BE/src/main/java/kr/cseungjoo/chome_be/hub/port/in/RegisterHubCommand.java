package kr.cseungjoo.chome_be.hub.port.in;

public record RegisterHubCommand(String serialNumber, String alias, long ownerId) {
}
