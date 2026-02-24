package kr.cseungjoo.chome_be.hub.application.result;

import java.time.Instant;

public record ChangeHubAliasResult(
        String alias,
        Instant changedAt
) {
}
