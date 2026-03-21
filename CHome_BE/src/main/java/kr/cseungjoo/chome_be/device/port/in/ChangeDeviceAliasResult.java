package kr.cseungjoo.chome_be.device.port.in;

import java.time.Instant;

public record ChangeDeviceAliasResult(
        String alias,
        Instant changedAt
) {
}
