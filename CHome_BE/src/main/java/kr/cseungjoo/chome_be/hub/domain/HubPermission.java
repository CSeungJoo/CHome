package kr.cseungjoo.chome_be.hub.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HubPermission {
    private final Long id;
    private final HubAction action;
    private final long hubId;
    private final long userId;

    public static HubPermission restore(Long id, HubAction action, long hubId, long userId) {
        if (id == null) {
            throw new IllegalStateException("id is null");
        }
        HubPermission hubPermission = new HubPermission(id, action, hubId, userId);

        return hubPermission;
    }

    public static HubPermission create(HubAction action, long hubId, long userId) {
        HubPermission hubPermission = new HubPermission(null, action, hubId, userId);

        return hubPermission;
    }
}