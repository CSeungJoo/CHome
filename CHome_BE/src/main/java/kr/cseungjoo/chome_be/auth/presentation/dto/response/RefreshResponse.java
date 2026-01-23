package kr.cseungjoo.chome_be.auth.presentation.dto.response;

public record RefreshResponse(
        String accessToken,
        String refreshToken
) {}
