package kr.cseungjoo.chome_be.hub.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaHubPermissionRepository extends JpaRepository<HubPermissionEntity, Long> {
    List<HubPermissionEntity> findByHubIdInAndUserId(List<Long> hubId, long userId);
}
