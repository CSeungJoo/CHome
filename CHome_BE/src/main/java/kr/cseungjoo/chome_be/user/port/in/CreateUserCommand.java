package kr.cseungjoo.chome_be.user.port.in;

public record CreateUserCommand(String name, String email, String password) {}
