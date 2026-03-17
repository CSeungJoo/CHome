package kr.cseungjoo.chome_be.device.port.in;

public interface FindAccessibleDeviceUseCase {
    FindAccessibleDeviceResult execute(FindAccessibleDeviceCommand command);
}
