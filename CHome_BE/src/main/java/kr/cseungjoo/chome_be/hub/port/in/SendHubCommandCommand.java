package kr.cseungjoo.chome_be.hub.port.in;

import java.util.Map;

public record SendHubCommandCommand(
        long userId,
        long hubId,
        String type,
        Map<String, Object> payload
) {}
