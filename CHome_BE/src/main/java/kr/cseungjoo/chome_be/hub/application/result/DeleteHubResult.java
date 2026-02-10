package kr.cseungjoo.chome_be.hub.application.result;

import java.time.Instant;

public record DeleteHubResult(
        String serialNumber,
        Instant deletedAt
) {
}
