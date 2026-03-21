package kr.cseungjoo.chome_be.auth.port.in;

public interface LoginUseCase {
    LoginResult execute(LoginCommand command);
}
