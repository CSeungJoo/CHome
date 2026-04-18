package kr.cseungjoo.chome_be.hub.adapter.web.dto.response;

import kr.cseungjoo.chome_be.hub.domain.HubAction;

import java.util.List;

public record InviteHubResponse(
        String hubAlias,
        String targetEmail,
        List<HubAction> permissions
) {
}
