package kr.cseungjoo.chome_be.user.domain;

import kr.cseungjoo.chome_be.user.domain.exception.AlreadyVerifiedException;
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

    public static User restore(Long id, String name, String email, String password, Instant emailVerifyAt, Instant createdAt, Instant lastLogin) {
        if (id == null || createdAt == null) {
            throw new IllegalStateException("id or createdAt is null");
        }
        User user = new User(id, name, email, password, emailVerifyAt, createdAt, lastLogin);

        return user;
    }

    public static User create(String name, String email, String password) {
        if (name.length() < 2) {
            throw new NameRuleViolationException("이름은 최소 2자 이상이여야 합니다.");
        }
        User user = new User(null, name, email, password, null, Instant.now(), null);

        return user;
    }

    public void changeName(String name) {
        if (name.length() < 2) {
            throw new NameRuleViolationException("이름은 최소 2자 이상이여야 합니다.");
        }
        this.name = name;
    }

    public boolean isEmailVerified() {
        return this.emailVerifyAt != null;
    }

    public void verifyEmail() {
        if (this.emailVerifyAt != null) {
            throw new AlreadyVerifiedException("이미 인증된 이메일입니다.");
        }
        this.emailVerifyAt = Instant.now();
    }
}
