package kr.cseungjoo.chome_be.device.port.in;

import java.util.List;

public record FindDeviceDetailResult(
        long id,
        String serialNumber,
        String name,
        String type,
        String alias,
        List<DeviceCommandInfo> commands
) {
    public record DeviceCommandInfo(
            long id,
            String command,
            String description
    ) {}
}
