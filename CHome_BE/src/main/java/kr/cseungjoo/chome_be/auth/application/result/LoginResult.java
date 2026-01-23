package kr.cseungjoo.chome_be.auth.application.result;

public record LoginResult(
        String accessToken,
        String refreshToken,
        Long userId
) {}
