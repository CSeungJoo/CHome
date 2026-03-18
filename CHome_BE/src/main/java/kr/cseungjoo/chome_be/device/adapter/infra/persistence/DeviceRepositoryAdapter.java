package kr.cseungjoo.chome_be.device.adapter.infra.persistence;

import jakarta.persistence.EntityManager;
import kr.cseungjoo.chome_be.device.domain.Device;
import kr.cseungjoo.chome_be.device.port.out.DeviceRepositoryPort;
import kr.cseungjoo.chome_be.hub.adapter.infra.persistence.HubEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DeviceRepositoryAdapter implements DeviceRepositoryPort {

    private final EntityManager em;
    private final JpaDeviceRepository jpaDeviceRepository;

    @Override
    public List<Device> findByHubId(long hubId) {
        return jpaDeviceRepository.findByHubId(hubId)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Device> findById(long deviceId) {
        return jpaDeviceRepository.findById(deviceId).map(this::toDomain);
    }

    @Override
    public Device save(Device device) {
        DeviceEntity deviceEntity = jpaDeviceRepository.save(toEntity(device));

        return toDomain(deviceEntity);
    }

    private DeviceEntity toEntity(Device device) {
        DeviceEntity deviceEntity = new DeviceEntity(
                device.getId(),
                device.getSerialNumber(),
                device.getName(),
                device.getType(),
                device.getAlias(),
                em.getReference(HubEntity.class, device.getHubId()),
                device.getCreatedAt()
        );

        return deviceEntity;
    }

    private Device toDomain(DeviceEntity deviceEntity) {
        Device device = Device.restore(
                deviceEntity.getId(),
                deviceEntity.getSerialNumber(),
                deviceEntity.getName(),
                deviceEntity.getType(),
                deviceEntity.getAlias(),
                deviceEntity.getHub().getId(),
                deviceEntity.getCreatedAt()
        );

        return device;
    }
}
