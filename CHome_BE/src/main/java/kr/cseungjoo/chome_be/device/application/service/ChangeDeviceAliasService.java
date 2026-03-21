package kr.cseungjoo.chome_be.device.application.service;

import kr.cseungjoo.chome_be.device.application.exception.DeviceNotFoundException;
import kr.cseungjoo.chome_be.device.domain.Device;
import kr.cseungjoo.chome_be.device.domain.DevicePermission;
import kr.cseungjoo.chome_be.device.port.in.ChangeDeviceAliasCommand;
import kr.cseungjoo.chome_be.device.port.in.ChangeDeviceAliasResult;
import kr.cseungjoo.chome_be.device.port.in.ChangeDeviceAliasUseCase;
import kr.cseungjoo.chome_be.device.port.out.DevicePermissionRepositoryPort;
import kr.cseungjoo.chome_be.device.port.out.DeviceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChangeDeviceAliasService implements ChangeDeviceAliasUseCase {

    private final DeviceRepositoryPort deviceRepositoryPort;
    private final DevicePermissionRepositoryPort devicePermissionRepositoryPort;

    @Override
    public ChangeDeviceAliasResult execute(ChangeDeviceAliasCommand command) {
        Device device = deviceRepositoryPort.findById(command.deviceId()).orElseThrow(
                () -> new DeviceNotFoundException("디바이스를 찾을 수 없습니다.")
        );

        List<DevicePermission> permissions = devicePermissionRepositoryPort.findByUserIdAndDeviceId(command.userId(), command.deviceId());

        device.assertUpdatableBy(command.userId(), permissions);

        device.renameAlias(command.alias());

        deviceRepositoryPort.save(device);

        return new ChangeDeviceAliasResult(
                device.getAlias(),
                Instant.now()
        );
    }
}
