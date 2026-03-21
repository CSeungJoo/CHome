package kr.cseungjoo.chome_be.hub.port.in;

public interface FindAccessibleHubsUseCase {
    FindAccessibleHubsResult execute(FindAccessibleHubsCommand command);
}
