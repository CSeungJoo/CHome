package kr.cseungjoo.chome_be.device.port.in;

public record FindDeviceDetailCommand(
        long userId,
        long deviceId
) {}
