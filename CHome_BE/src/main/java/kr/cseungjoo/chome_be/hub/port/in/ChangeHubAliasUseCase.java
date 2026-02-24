package kr.cseungjoo.chome_be.hub.port.in;

import kr.cseungjoo.chome_be.hub.application.command.ChangeHubAliasCommand;
import kr.cseungjoo.chome_be.hub.application.result.ChangeHubAliasResult;

public interface ChangeHubAliasUseCase {
     ChangeHubAliasResult execute(ChangeHubAliasCommand command);
}
