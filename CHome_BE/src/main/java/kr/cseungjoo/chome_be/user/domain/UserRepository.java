package kr.cseungjoo.chome_be.user.domain;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(String email);

    User save(User user);
}
