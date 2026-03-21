package kr.cseungjoo.chome_be.hub.port.in;

public interface SendHubCommandUseCase {
    SendHubCommandResult execute(SendHubCommandCommand command);
}
