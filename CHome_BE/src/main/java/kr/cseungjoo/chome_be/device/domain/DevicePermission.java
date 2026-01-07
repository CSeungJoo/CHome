package kr.cseungjoo.chome_be.device.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DevicePermission {
    private long id;
    private final DeviceAction action;
    private final long deviceId;
    private final long userId;

    static DevicePermission restore(
            Long id,
            DeviceAction action,
            long deviceId,
            long userId
    ) {
        if (id == null) {
            throw new IllegalStateException("id is null");
        }
        DevicePermission devicePermission = new DevicePermission(id, action, deviceId, userId);

        return devicePermission;
    }
}
