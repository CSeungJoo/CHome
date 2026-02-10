package kr.cseungjoo.chome_be.hub.port.out;

import kr.cseungjoo.chome_be.hub.domain.HubPermission;

import java.util.List;

public interface HubPermissionRepositoryPort {
    List<HubPermission> findByUserIdAndHubIdIn(long userId, List<Long> hubIds);
    List<HubPermission> findByUserIdAndHubId(long userId, long hubId);
}
