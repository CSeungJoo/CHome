package kr.cseungjoo.chome_be.hub.application.port.out;

import kr.cseungjoo.chome_be.hub.domain.Hub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface HubRepositoryPort {
    Optional<Hub> findById(long hubId);
    Page<Hub> findByUserId(long userId, Pageable pageable);
    boolean exists(String serialNumber);
    Hub save(Hub hub);
}
