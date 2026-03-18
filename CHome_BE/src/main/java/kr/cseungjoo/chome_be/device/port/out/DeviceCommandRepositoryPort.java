package kr.cseungjoo.chome_be.device.port.out;

import kr.cseungjoo.chome_be.device.domain.DeviceCommand;

import java.util.List;

public interface DeviceCommandRepositoryPort {
    List<DeviceCommand> findByDeviceId(long deviceId);
}
