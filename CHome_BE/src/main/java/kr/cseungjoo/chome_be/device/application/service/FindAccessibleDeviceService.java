package kr.cseungjoo.chome_be.device.application.service;

import kr.cseungjoo.chome_be.device.domain.Device;
import kr.cseungjoo.chome_be.device.domain.DevicePermission;
import kr.cseungjoo.chome_be.device.port.in.FindAccessibleDeviceCommand;
import kr.cseungjoo.chome_be.device.port.in.FindAccessibleDeviceResult;
import kr.cseungjoo.chome_be.device.port.in.FindAccessibleDeviceUseCase;
import kr.cseungjoo.chome_be.device.port.out.DevicePermissionRepositoryPort;
import kr.cseungjoo.chome_be.device.port.out.DeviceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindAccessibleDeviceService implements FindAccessibleDeviceUseCase {

    private final DeviceRepositoryPort deviceRepositoryPort;
    private final DevicePermissionRepositoryPort devicePermissionRepositoryPort;

    @Override
    public FindAccessibleDeviceResult execute(FindAccessibleDeviceCommand command) {
        List<Device> devices = deviceRepositoryPort.findByHubId(command.hubId());

        List<Long> deviceIds = devices.stream()
                .map(Device::getId)
                .toList();

        List<DevicePermission> devicePermissions = devicePermissionRepositoryPort.findByUserIdAndDeviceIds(command.userId(), deviceIds);

        Map<Long, List<DevicePermission>> permissionByDeviceId = devicePermissions.stream()
                .collect(Collectors.groupingBy(DevicePermission::getDeviceId));

        List<FindAccessibleDeviceResult.AccessibleDevice> accessibleDevices = devices.stream()
                .filter(device ->
                        device.canReadBy(
                                command.userId(),
                                permissionByDeviceId.getOrDefault(device.getId(), List.of())
                        )
                )
                .map(device -> new FindAccessibleDeviceResult.AccessibleDevice(
                        device.getId(),
                        device.getSerialNumber(),
                        device.getName(),
                        device.getType(),
                        device.getAlias()
                ))
                .toList();

        return new FindAccessibleDeviceResult(accessibleDevices);
    }
}
