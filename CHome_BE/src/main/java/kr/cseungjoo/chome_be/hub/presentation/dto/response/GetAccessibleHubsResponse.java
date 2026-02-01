package kr.cseungjoo.chome_be.hub.presentation.dto.response;

import java.util.List;

public record GetAccessibleHubsResponse(
        List<AccessibleHub> accessibleHubs,
        long totalCount,
        int page,
        int size,
        boolean hasNext
) {

    public record AccessibleHub(long id, String serialNumber, String alias, boolean isOwner) {
    }
}
