package kr.cseungjoo.chome_be.auth.port.in;

public record RefreshResult(
        String accessToken,
        String refreshToken
) {}
