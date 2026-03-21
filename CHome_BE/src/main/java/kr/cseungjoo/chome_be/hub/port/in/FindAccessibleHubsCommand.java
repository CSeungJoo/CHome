package kr.cseungjoo.chome_be.hub.port.in;

import org.springframework.data.domain.Pageable;

public record FindAccessibleHubsCommand(long userId,Pageable pageable) {
}
