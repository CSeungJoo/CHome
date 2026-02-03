package kr.cseungjoo.chome_be.hub.port.in;

import kr.cseungjoo.chome_be.hub.application.command.FindAccessibleHubsCommand;
import kr.cseungjoo.chome_be.hub.application.result.FindAccessibleHubsResult;

public interface FindAccessibleHubsUseCase {
    FindAccessibleHubsResult execute(FindAccessibleHubsCommand command);
}
