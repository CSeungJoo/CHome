package kr.cseungjoo.chome_be.hub.application.result;

import java.time.Instant;

public record RegisterHubResult(String serialNumber, String alias, Instant createdAt) {
}
