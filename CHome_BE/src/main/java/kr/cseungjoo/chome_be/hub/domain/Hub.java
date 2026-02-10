package kr.cseungjoo.chome_be.hub.domain;

import kr.cseungjoo.chome_be.hub.domain.exception.HubPermissionDeniedException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Hub {
    private Long id;
    private String serialNumber;
    private String alias;
    private long ownerId;
    private Instant createdAt;

    public static Hub restore(
            Long id,
            String serialNumber,
            String alias,
            long ownerId,
            Instant createdAt
    ) {
        if (id == null || createdAt == null) {
            throw new IllegalStateException("id or createdAt is null");
        }
        Hub hub = new Hub(id, serialNumber, alias, ownerId, createdAt);

        return hub;
    }

    public static Hub create(String serialNumber, String alias, long ownerId) {
        Hub hub = new Hub(null, serialNumber, alias, ownerId, Instant.now());

        return hub;
    }

    public boolean canReadBy(long userId, List<HubPermission> permissions) {
        return userId == ownerId
                || permissions.stream().anyMatch(p ->
                p.getAction() == HubAction.READ
        );
    }

    public void assertDeletableBy(long userId, List<HubPermission> permissions) {
        boolean deletable = userId == ownerId
                || permissions.stream().anyMatch(p ->
                p.getAction() == HubAction.DELETE);

        if (!deletable) {
            throw new HubPermissionDeniedException("삭제 권한이 없습니다.");
        }
    }

    public boolean isOwner(long userId) {
        return userId == ownerId;
    }
}
