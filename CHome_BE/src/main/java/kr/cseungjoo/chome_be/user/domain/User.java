package kr.cseungjoo.chome_be.user.domain;

import kr.cseungjoo.chome_be.user.domain.exception.NameRuleViolationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Instant emailVerifyAt;
    private Instant createdAt;
    private Instant lastLogin;

    static User restore(Long id, String name, String email, String password, Instant emailVerifyAt, Instant createdAt, Instant lastLogin) {
        if (id == null || createdAt == null) {
            throw new IllegalStateException("id or createdAt is null");
        }
        User user = new User(id, name, email, password, emailVerifyAt, createdAt, lastLogin);

        return user;
    }

    public static User create(String name, String email, String password) {
        if (name.length() > 2) {
            throw new NameRuleViolationException("이름은 최소 2자 이상이여야 합니다.");
        }
        User user = new User(null, name, email, password, null, Instant.now(), null);

        return user;
    }
}
