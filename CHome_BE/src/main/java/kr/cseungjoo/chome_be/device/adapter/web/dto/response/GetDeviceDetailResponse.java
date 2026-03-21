package kr.cseungjoo.chome_be.device.adapter.web.dto.response;

import java.util.List;

public record GetDeviceDetailResponse(
        long id,
        String serialNumber,
        String name,
        String type,
        String alias,
        List<CommandInfo> commands
) {
    public record CommandInfo(
            long id,
            String command,
            String description
    ) {}
}
