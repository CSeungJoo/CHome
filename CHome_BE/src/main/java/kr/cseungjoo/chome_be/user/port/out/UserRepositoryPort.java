package kr.cseungjoo.chome_be.user.port.out;

import kr.cseungjoo.chome_be.user.domain.User;

import java.util.Optional;

public interface UserRepositoryPort {

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    boolean exists(String email);

    User save(User user);
}
