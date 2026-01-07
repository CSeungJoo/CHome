package kr.cseungjoo.chome_be.hub.domain;


import java.util.List;
import java.util.Optional;

public interface HubRepository {
    Optional<Hub> findById(long hubId);
    List<Hub> findByUserId(long userId);
    Hub save(Hub hub);
}
