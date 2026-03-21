package kr.cseungjoo.chome_be.user.port.in;

import java.time.Instant;

public record CreateUserResult(
        String name,
        String email,
        boolean EmailVerified,
        Instant createdAt,
        Instant lastLogin
) {}
