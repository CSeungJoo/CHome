package kr.cseungjoo.chome_be.device.port.in;

public interface FindDeviceDetailUseCase {
    FindDeviceDetailResult execute(FindDeviceDetailCommand command);
}
