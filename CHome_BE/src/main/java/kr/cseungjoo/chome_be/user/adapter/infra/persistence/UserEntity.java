package kr.cseungjoo.chome_be.user.adapter.infra.persistence;

import jakarta.persistence.*;
import kr.cseungjoo.chome_be.user.domain.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String password;

    @Enumerated(EnumType.STRING)
    @Column
    private Role role;

    @Column
    private Instant emailVerifyAt;

    @Column
    private Instant createdAt;

    @Column
    private Instant lastLogin;
}
