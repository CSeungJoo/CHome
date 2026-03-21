package kr.cseungjoo.chome_be.auth.port.in;

public record LoginResult(
        String accessToken,
        String refreshToken,
        Long userId
) {}
