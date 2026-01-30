package kr.cseungjoo.chome_be.hub.application.port.in;

import kr.cseungjoo.chome_be.hub.application.command.RegisterHubCommand;
import kr.cseungjoo.chome_be.hub.application.result.RegisterHubResult;

public interface RegisterHubUseCase {
    RegisterHubResult execute(RegisterHubCommand command);
}
