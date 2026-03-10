package kr.cseungjoo.chome_be.auth.port.in;

public record RefreshCommand(
        String refreshToken
) {}
