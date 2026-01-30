package kr.cseungjoo.chome_be.hub.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaHubPermissionRepository extends JpaRepository<HubPermissionEntity, Long> {
}
