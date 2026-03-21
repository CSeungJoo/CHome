package kr.cseungjoo.chome_be.device.port.in;

import java.util.List;

public record FindAccessibleDeviceResult(
    List<AccessibleDevice> devices
) {

    public record AccessibleDevice(long id, String serialNumber, String name, String type, String alias) {}
}
