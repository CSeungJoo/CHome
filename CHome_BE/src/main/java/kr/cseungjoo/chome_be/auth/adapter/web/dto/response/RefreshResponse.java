package kr.cseungjoo.chome_be.auth.adapter.web.dto.response;

public record RefreshResponse(
        String accessToken,
        String refreshToken
) {}
