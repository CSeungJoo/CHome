package kr.cseungjoo.chome_be.device.port.out;

import kr.cseungjoo.chome_be.device.domain.Device;

import java.util.List;
import java.util.Optional;

public interface DeviceRepositoryPort {
    List<Device> findByHubId(long hubId);
    Optional<Device> findById(long deviceId);
    Device save(Device device);
}
