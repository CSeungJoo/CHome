package kr.cseungjoo.chome_be.hub.port.in;

import kr.cseungjoo.chome_be.hub.domain.HubAction;

import java.util.List;

public record InviteHubCommand(
        long userId,
        long hubId,
        String targetEmail,
        List<HubAction> permissions
) {
}