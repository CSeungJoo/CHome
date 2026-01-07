package kr.cseungjoo.chome_be.hub.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Hub {
    private Long id;
    private String serialNumber;
    private String alias;
    private long ownerId;
    private Instant createdAt;

    static Hub restore(
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
}
