package kr.cseungjoo.chome_be.auth.application.result;

public record RefreshResult(
        String accessToken,
        String refreshToken
) {}
