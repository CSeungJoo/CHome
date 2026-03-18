package kr.cseungjoo.chome_be.device.adapter.web.dto.response;

import java.time.Instant;

public record ChangeDeviceAliasResponse(
        String changedAlias,
        Instant changedAt
) {
}
