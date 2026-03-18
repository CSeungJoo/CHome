package kr.cseungjoo.chome_be.auth.port.in;

public record AuthenticateHubCommand(
        String clientId,
        String username,
        String password
) {}
