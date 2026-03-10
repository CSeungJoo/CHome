package kr.cseungjoo.chome_be.hub.port.in;

public interface DeleteHubUseCase {
    DeleteHubResult execute(DeleteHubCommand command);
}
