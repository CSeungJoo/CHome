package kr.cseungjoo.chome_be.user.application.command;

public record CreateUserCommand(String name, String email, String password) {}
