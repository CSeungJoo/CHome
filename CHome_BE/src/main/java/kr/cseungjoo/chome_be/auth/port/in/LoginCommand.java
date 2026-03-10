package kr.cseungjoo.chome_be.auth.port.in;

public record LoginCommand(
        String email,
        String password
) {}
