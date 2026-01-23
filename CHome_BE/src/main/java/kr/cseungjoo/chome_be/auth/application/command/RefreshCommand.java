package kr.cseungjoo.chome_be.auth.application.command;

public record RefreshCommand(
        String refreshToken
) {}
