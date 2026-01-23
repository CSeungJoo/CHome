package kr.cseungjoo.chome_be.auth.application.command;

public record LoginCommand(
        String email,
        String password
) {}
