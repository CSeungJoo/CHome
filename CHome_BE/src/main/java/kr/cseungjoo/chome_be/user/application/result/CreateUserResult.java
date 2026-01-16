package kr.cseungjoo.chome_be.user.application.result;

import java.time.Instant;

public record CreateUserResult(
        String name,
        String email,
        boolean EmailVerified,
        Instant createdAt,
        Instant lastLogin
) {}
