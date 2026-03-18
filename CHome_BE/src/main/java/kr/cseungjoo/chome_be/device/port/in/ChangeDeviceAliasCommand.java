package kr.cseungjoo.chome_be.device.port.in;

public record ChangeDeviceAliasCommand(
        Long deviceId,
        String alias,
        Long userId
) {
}
