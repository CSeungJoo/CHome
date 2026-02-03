package kr.cseungjoo.chome_be.hub.adapter.infra.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaHubRepository extends JpaRepository<HubEntity, Long> {
    @Query("""
        select h
        from HubEntity h
        where h.owner.id = :userId
           or exists (
               select 1
               from HubPermissionEntity hp
               where hp.hub = h
                 and hp.user.id = :userId
           )
    """)
    Page<HubEntity> findByUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );

    boolean existsBySerialNumber(String serialNumber);
}
