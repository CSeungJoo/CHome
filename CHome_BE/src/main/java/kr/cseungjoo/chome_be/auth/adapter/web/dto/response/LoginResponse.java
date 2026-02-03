package kr.cseungjoo.chome_be.auth.adapter.web.dto.response;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        Long userId
) {}
