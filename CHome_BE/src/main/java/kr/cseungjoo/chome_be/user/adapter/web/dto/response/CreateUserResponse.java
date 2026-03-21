package kr.cseungjoo.chome_be.user.adapter.web.dto.response;

import java.time.Instant;

public record CreateUserResponse(
        String name,
        String email,
        boolean emailVerify,
        Instant createdAt,
        Instant lastLogin
) {}