package kr.cseungjoo.chome_be.hub.adapter.infra.persistence;

import jakarta.persistence.EntityManager;
import kr.cseungjoo.chome_be.hub.port.out.HubPermissionRepositoryPort;
import kr.cseungjoo.chome_be.hub.domain.HubPermission;
import kr.cseungjoo.chome_be.user.adapter.infra.persistence.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class HubPermissionRepositoryAdapter implements HubPermissionRepositoryPort {

    private final EntityManager em;
    private final JpaHubPermissionRepository jpaHubPermissionRepository;

    @Override
    public List<HubPermission> findByUserIdAndHubIdIn(long userId, List<Long> hubIds) {
        List<HubPermissionEntity> hubPermissionEntities = jpaHubPermissionRepository.findByHubIdInAndUserId(hubIds, userId);

        return hubPermissionEntities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private HubPermissionEntity toEntity(HubPermission hubPermission) {
        HubPermissionEntity hubPermissionEntity = new HubPermissionEntity(
                hubPermission.getId(),
                hubPermission.getAction(),
                em.getReference(UserEntity.class, hubPermission.getUserId()),
                em.getReference(HubEntity.class, hubPermission.getHubId())
        );

        return hubPermissionEntity;
    }

    private HubPermission toDomain(HubPermissionEntity hubPermissionEntity) {
        HubPermission hubPermission = HubPermission.restore(
                hubPermissionEntity.getId(),
                hubPermissionEntity.getAction(),
                hubPermissionEntity.getHub().getId(),
                hubPermissionEntity.getUser().getId()
        );

        return hubPermission;
    }
}
