package kr.cseungjoo.chome_be.hub.port.in;

public interface RegisterHubUseCase {
    RegisterHubResult execute(RegisterHubCommand command);
}
