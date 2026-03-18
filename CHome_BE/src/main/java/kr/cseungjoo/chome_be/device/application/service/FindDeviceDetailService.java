package kr.cseungjoo.chome_be.device.application.service;

import kr.cseungjoo.chome_be.device.application.exception.DeviceNotFoundException;
import kr.cseungjoo.chome_be.device.domain.Device;
import kr.cseungjoo.chome_be.device.domain.DeviceCommand;
import kr.cseungjoo.chome_be.device.domain.DevicePermission;
import kr.cseungjoo.chome_be.device.port.in.FindDeviceDetailCommand;
import kr.cseungjoo.chome_be.device.port.in.FindDeviceDetailResult;
import kr.cseungjoo.chome_be.device.port.in.FindDeviceDetailUseCase;
import kr.cseungjoo.chome_be.device.port.out.DeviceCommandRepositoryPort;
import kr.cseungjoo.chome_be.device.port.out.DevicePermissionRepositoryPort;
import kr.cseungjoo.chome_be.device.port.out.DeviceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindDeviceDetailService implements FindDeviceDetailUseCase {

    private final DeviceRepositoryPort deviceRepositoryPort;
    private final DevicePermissionRepositoryPort devicePermissionRepositoryPort;
    private final DeviceCommandRepositoryPort deviceCommandRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public FindDeviceDetailResult execute(FindDeviceDetailCommand command) {
        Device device = deviceRepositoryPort.findById(command.deviceId())
                .orElseThrow(() -> new DeviceNotFoundException("디바이스를 찾을 수 없습니다."));

        List<DevicePermission> permissions = devicePermissionRepositoryPort
                .findByUserIdAndDeviceId(command.userId(), command.deviceId());

        if (!device.canReadBy(command.userId(), permissions)) {
            throw new DeviceNotFoundException("디바이스를 찾을 수 없습니다.");
        }

        List<DeviceCommand> commands = deviceCommandRepositoryPort.findByDeviceId(command.deviceId());

        List<FindDeviceDetailResult.DeviceCommandInfo> commandInfos = commands.stream()
                .map(c -> new FindDeviceDetailResult.DeviceCommandInfo(
                        c.getId(),
                        c.getCommand(),
                        c.getDescription()
                ))
                .toList();

        return new FindDeviceDetailResult(
                device.getId(),
                device.getSerialNumber(),
                device.getName(),
                device.getType(),
                device.getAlias(),
                commandInfos
        );
    }
}
