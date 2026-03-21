package kr.cseungjoo.chome_be.device.port.in;

public record FindAccessibleDeviceCommand(
        long hubId,
        long userId
) {
}
