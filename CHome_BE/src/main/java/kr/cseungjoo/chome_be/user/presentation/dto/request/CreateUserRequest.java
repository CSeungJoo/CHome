package kr.cseungjoo.chome_be.user.presentation.dto.request;

public record CreateUserRequest(
        String name,
        String email,
        String password
) {}
