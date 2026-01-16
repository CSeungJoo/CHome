package kr.cseungjoo.chome_be.user.application.port.in;

public interface VerifyEmailUseCase {
    void execute(String token);
}
