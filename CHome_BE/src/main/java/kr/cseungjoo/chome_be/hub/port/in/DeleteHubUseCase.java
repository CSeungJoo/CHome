package kr.cseungjoo.chome_be.hub.port.in;

import kr.cseungjoo.chome_be.hub.application.command.DeleteHubCommand;
import kr.cseungjoo.chome_be.hub.application.result.DeleteHubResult;

public interface DeleteHubUseCase {
    DeleteHubResult execute(DeleteHubCommand command);
}
