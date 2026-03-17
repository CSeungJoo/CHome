package kr.cseungjoo.chome_be.device.port.out;

import kr.cseungjoo.chome_be.device.domain.DevicePermission;

import java.util.List;

public interface DevicePermissionRepositoryPort {
    List<DevicePermission> findByUserIdAndDeviceIds(Long userId, List<Long> deviceId);
    DevicePermission save(DevicePermission devicePermission);
}
