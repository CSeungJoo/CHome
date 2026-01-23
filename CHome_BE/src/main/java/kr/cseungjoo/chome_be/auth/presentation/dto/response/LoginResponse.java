package kr.cseungjoo.chome_be.auth.presentation.dto.response;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        Long userId
) {}
