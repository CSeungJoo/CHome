package kr.cseungjoo.chome_be.hub.port.in;

import java.time.Instant;

public record DeleteHubResult(
        String serialNumber,
        Instant deletedAt
) {
}
