package kr.cseungjoo.chome_be.device.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeviceCommand {
    private Long id;
    private String command;
    private String description;
    private long deviceId;

    public static DeviceCommand restore(Long id, String command, String description, long deviceId) {
        if (id == null) {
            throw new IllegalStateException("id is null");
        }
        return new DeviceCommand(id, command, description, deviceId);
    }

    public static DeviceCommand create(String command, String description, long deviceId) {
        return new DeviceCommand(null, command, description, deviceId);
    }
}
