package kr.cseungjoo.chome_be.auth.port.in;

public interface AuthenticateHubUseCase {
    void execute(AuthenticateHubCommand command);
}
