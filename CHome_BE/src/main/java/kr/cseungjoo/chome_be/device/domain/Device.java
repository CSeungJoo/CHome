package kr.cseungjoo.chome_be.device.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Device {
    private Long id;
    private String serialNumber;
    private String name;
    private String type;
    private String alias;
    private long hubId;
    private Instant createdAt;

    public static Device restore(
            Long id,
            String serialNumber,
            String name,
            String type,
            String alias,
            long hubId,
            Instant createdAt
    ) {
        if (id == null || createdAt == null) {
            throw new IllegalStateException("id or createdAt is null");
        }
        Device device = new Device(id, serialNumber, name, type, alias, hubId, createdAt);

        return device;
    }

    public static Device create(String serialNumber, String name, String type, String alias, long hubId) {
        Device device = new Device(null, serialNumber, name, type, alias, hubId, Instant.now());

        return device;
    }

    public boolean canReadBy(long userId, List<DevicePermission> permissions) {
        return permissions.stream().anyMatch(p ->
                p.getAction() == DeviceAction.READ && p.getUserId() == userId
        );
    }
}
