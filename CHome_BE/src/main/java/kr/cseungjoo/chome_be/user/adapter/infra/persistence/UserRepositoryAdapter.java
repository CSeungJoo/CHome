package kr.cseungjoo.chome_be.user.adapter.infra.persistence;

import kr.cseungjoo.chome_be.user.domain.User;
import kr.cseungjoo.chome_be.user.application.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(this::toDomain);
    }

    @Override
    public boolean exists(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity save = jpaUserRepository.save(entity);

        return toDomain(save);
    }

    private UserEntity toEntity(User user) {
        UserEntity userEntity = new UserEntity(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getEmailVerifyAt(),
                user.getCreatedAt(),
                user.getLastLogin()
        );

        return userEntity;
    }

    private User toDomain(UserEntity userEntity) {
        User user = User.restore(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getRole(),
                userEntity.getEmailVerifyAt(),
                userEntity.getCreatedAt(),
                userEntity.getLastLogin()
        );

        return user;
    }

}
