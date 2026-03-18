package kr.cseungjoo.chome_be.device.adapter.infra.persistence;

import jakarta.persistence.EntityManager;
import kr.cseungjoo.chome_be.device.domain.DevicePermission;
import kr.cseungjoo.chome_be.device.port.out.DevicePermissionRepositoryPort;
import kr.cseungjoo.chome_be.user.adapter.infra.persistence.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DevicePermissionRepositoryAdapter implements DevicePermissionRepositoryPort {

    private final EntityManager em;
    private final JpaDevicePermissionRepository jpaDevicePermissionRepository;

    @Override
    public List<DevicePermission> findByUserIdAndDeviceIds(Long userId, List<Long> deviceIds) {
        return jpaDevicePermissionRepository.findByUserIdAndDeviceIds(userId, deviceIds)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<DevicePermission> findByUserIdAndDeviceId(long userId, long deviceId) {
        return jpaDevicePermissionRepository.findByUserIdAndDeviceIds(userId, List.of(deviceId))
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public DevicePermission save(DevicePermission devicePermission) {
        DevicePermissionEntity devicePermissionEntity = jpaDevicePermissionRepository.save(toEntity(devicePermission));

        return toDomain(devicePermissionEntity);
    }

    private DevicePermission toDomain(DevicePermissionEntity devicePermissionEntity) {
        DevicePermission devicePermission = DevicePermission.restore(
                devicePermissionEntity.getId(),
                devicePermissionEntity.getAction(),
                devicePermissionEntity.getDevice().getId(),
                devicePermissionEntity.getUser().getId()
        );

        return devicePermission;
    }

    private DevicePermissionEntity toEntity(DevicePermission devicePermission) {
        DevicePermissionEntity devicePermissionEntity = new DevicePermissionEntity(
                devicePermission.getId(),
                devicePermission.getAction(),
                em.getReference(DeviceEntity.class, devicePermission.getDeviceId()),
                em.getReference(UserEntity.class, devicePermission.getUserId())
        );

        return devicePermissionEntity;
    }
}
