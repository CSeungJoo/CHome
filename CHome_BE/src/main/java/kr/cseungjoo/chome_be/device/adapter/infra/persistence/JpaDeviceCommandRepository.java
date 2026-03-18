package kr.cseungjoo.chome_be.device.adapter.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaDeviceCommandRepository extends JpaRepository<DeviceCommandEntity, Long> {
    List<DeviceCommandEntity> findByDeviceId(Long deviceId);
}
