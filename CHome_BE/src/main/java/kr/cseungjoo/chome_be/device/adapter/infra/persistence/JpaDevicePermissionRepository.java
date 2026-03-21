package kr.cseungjoo.chome_be.device.adapter.infra.persistence;

import kr.cseungjoo.chome_be.user.adapter.infra.persistence.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaDevicePermissionRepository extends JpaRepository<DevicePermissionEntity, Long> {


    @Query("""
    select dp
    from DevicePermissionEntity dp
    where dp.user.id = :userId
    and dp.device.id in :deviceIds
    """)
    List<DevicePermissionEntity> findByUserIdAndDeviceIds(
            @Param("userId") Long userId,
            @Param("deviceIds") List<Long> deviceIds
    );

    Long user(UserEntity user);
}
