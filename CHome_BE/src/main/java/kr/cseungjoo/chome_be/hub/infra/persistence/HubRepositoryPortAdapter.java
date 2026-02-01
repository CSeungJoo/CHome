package kr.cseungjoo.chome_be.hub.infra.persistence;

import jakarta.persistence.EntityManager;
import kr.cseungjoo.chome_be.hub.application.port.out.HubRepositoryPort;
import kr.cseungjoo.chome_be.hub.domain.Hub;
import kr.cseungjoo.chome_be.hub.domain.HubAction;
import kr.cseungjoo.chome_be.user.infra.persistence.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HubRepositoryPortAdapter implements HubRepositoryPort {

    private final EntityManager em;
    private final JpaHubRepository jpaHubRepository;

    @Override
    public Optional<Hub> findById(long hubId) {
        Optional<Hub> hub = jpaHubRepository.findById(hubId)
                .map(this::toDomain);

        return hub;
    }

    @Override
    public Page<Hub> findByUserId(long userId, Pageable pageable) {
        Page<Hub> hubs = jpaHubRepository.findByUserId(userId, pageable)
                .map(this::toDomain);

        return hubs;
    }

    @Override
    public Hub save(Hub hub) {
        HubEntity hubEntity = toEntity(hub);

        hubEntity = jpaHubRepository.save(hubEntity);

        return toDomain(hubEntity);
    }

    @Override
    public boolean exists(String serialNumber) {
        boolean exists = jpaHubRepository.existsBySerialNumber(serialNumber);

        return exists;
    }

    private HubEntity toEntity(Hub hub) {
        HubEntity hubEntity = new HubEntity(
                hub.getId(),
                hub.getSerialNumber(),
                hub.getAlias(),
                hub.getCreatedAt(),
                em.getReference(UserEntity.class, hub.getOwnerId())
        );

        return hubEntity;
    }

    private Hub toDomain(HubEntity hubEntity) {
        Hub hub = Hub.restore(
                hubEntity.getId(),
                hubEntity.getSerialNumber(),
                hubEntity.getAlias(),
                hubEntity.getOwner().getId(),
                hubEntity.getCreatedAt()
        );

        return hub;
    }
}
