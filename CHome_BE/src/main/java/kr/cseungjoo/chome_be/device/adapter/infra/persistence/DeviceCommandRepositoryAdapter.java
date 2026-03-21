package kr.cseungjoo.chome_be.device.adapter.infra.persistence;

import kr.cseungjoo.chome_be.device.domain.DeviceCommand;
import kr.cseungjoo.chome_be.device.port.out.DeviceCommandRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DeviceCommandRepositoryAdapter implements DeviceCommandRepositoryPort {

    private final JpaDeviceCommandRepository jpaDeviceCommandRepository;

    @Override
    public List<DeviceCommand> findByDeviceId(long deviceId) {
        return jpaDeviceCommandRepository.findByDeviceId(deviceId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private DeviceCommand toDomain(DeviceCommandEntity entity) {
        return DeviceCommand.restore(
                entity.getId(),
                entity.getCommand(),
                entity.getDescription(),
                entity.getDevice().getId()
        );
    }
}
