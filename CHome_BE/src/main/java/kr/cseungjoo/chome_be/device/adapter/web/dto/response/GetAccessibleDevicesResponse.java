package kr.cseungjoo.chome_be.device.adapter.web.dto.response;

import java.util.List;

public record GetAccessibleDevicesResponse(
        List<AccessibleDevice> devices
) {

    public record AccessibleDevice(long id, String serialNumber, String name, String type, String alias) {}
}
