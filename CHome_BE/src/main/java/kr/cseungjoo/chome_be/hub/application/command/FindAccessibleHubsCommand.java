package kr.cseungjoo.chome_be.hub.application.command;

import org.springframework.data.domain.Pageable;

public record FindAccessibleHubsCommand(long userId,Pageable pageable) {
}
