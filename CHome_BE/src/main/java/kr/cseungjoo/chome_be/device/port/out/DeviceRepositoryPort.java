package kr.cseungjoo.chome_be.device.port.out;

import kr.cseungjoo.chome_be.device.domain.Device;

import java.util.List;

public interface DeviceRepositoryPort {
    List<Device> findByHubId(long hubId);
    Device save(Device device);
}
