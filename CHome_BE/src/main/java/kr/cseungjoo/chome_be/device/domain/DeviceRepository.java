package kr.cseungjoo.chome_be.device.domain;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository {
    Optional<Device> findById(long hubId);
    List<Device> findByUserId(long userId);
    Device save(Device device);
}
