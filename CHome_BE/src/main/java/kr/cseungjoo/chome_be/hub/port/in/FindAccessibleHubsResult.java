package kr.cseungjoo.chome_be.hub.port.in;

import java.util.List;

public record FindAccessibleHubsResult(
        List<AccessibleHub> hubs,
        long totalCount,
        int page,
        int size,
        boolean hasNext
) {

    public record AccessibleHub(long id, String serialNumber, String alias, boolean isOwner) {
    }
}
