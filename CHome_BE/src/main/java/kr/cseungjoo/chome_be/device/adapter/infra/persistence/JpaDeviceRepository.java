package kr.cseungjoo.chome_be.device.adapter.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaDeviceRepository extends JpaRepository<DeviceEntity, Long> {
    List<DeviceEntity> findByHubId(Long hubId);
}
