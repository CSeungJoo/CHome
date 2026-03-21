package kr.cseungjoo.chome_be.user.domain;

import kr.cseungjoo.chome_be.user.domain.exception.AlreadyVerifiedException;
import kr.cseungjoo.chome_be.user.domain.exception.NameRuleViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("유효한 정보로 User를 생성한다")
        void success() {
            User user = User.create("홍길동", "test@example.com", "encoded-pw");

            assertThat(user.getId()).isNull();
            assertThat(user.getName()).isEqualTo("홍길동");
            assertThat(user.getEmail()).isEqualTo("test@example.com");
            assertThat(user.getPassword()).isEqualTo("encoded-pw");
            assertThat(user.getRole()).isEqualTo(Role.USER);
            assertThat(user.getEmailVerifyAt()).isNull();
            assertThat(user.getCreatedAt()).isNotNull();
            assertThat(user.getLastLogin()).isNull();
        }

        @Test
        @DisplayName("이름이 2자 미만이면 NameRuleViolationException 발생")
        void failWhenNameTooShort() {
            assertThatThrownBy(() -> User.create("홍", "test@example.com", "pw"))
                    .isInstanceOf(NameRuleViolationException.class);
        }
    }

    @Nested
    @DisplayName("restore")
    class Restore {

        @Test
        @DisplayName("유효한 정보로 User를 복원한다")
        void success() {
            Instant now = Instant.now();
            User user = User.restore(1L, "홍길동", "test@example.com", "pw", Role.USER, now, now, now);

            assertThat(user.getId()).isEqualTo(1L);
            assertThat(user.getEmailVerifyAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("id가 null이면 IllegalStateException 발생")
        void failWhenIdNull() {
            Instant now = Instant.now();
            assertThatThrownBy(() -> User.restore(null, "홍길동", "test@example.com", "pw", Role.USER, null, now, null))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("createdAt이 null이면 IllegalStateException 발생")
        void failWhenCreatedAtNull() {
            assertThatThrownBy(() -> User.restore(1L, "홍길동", "test@example.com", "pw", Role.USER, null, null, null))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("changeName")
    class ChangeName {

        @Test
        @DisplayName("유효한 이름으로 변경한다")
        void success() {
            User user = User.create("홍길동", "test@example.com", "pw");
            user.changeName("김철수");

            assertThat(user.getName()).isEqualTo("김철수");
        }

        @Test
        @DisplayName("2자 미만 이름으로 변경 시 NameRuleViolationException 발생")
        void failWhenNameTooShort() {
            User user = User.create("홍길동", "test@example.com", "pw");

            assertThatThrownBy(() -> user.changeName("홍"))
                    .isInstanceOf(NameRuleViolationException.class);
        }
    }

    @Nested
    @DisplayName("verifyEmail")
    class VerifyEmail {

        @Test
        @DisplayName("이메일 인증에 성공한다")
        void success() {
            User user = User.create("홍길동", "test@example.com", "pw");

            assertThat(user.isEmailVerified()).isFalse();

            user.verifyEmail();

            assertThat(user.isEmailVerified()).isTrue();
            assertThat(user.getEmailVerifyAt()).isNotNull();
        }

        @Test
        @DisplayName("이미 인증된 이메일이면 AlreadyVerifiedException 발생")
        void failWhenAlreadyVerified() {
            User user = User.create("홍길동", "test@example.com", "pw");
            user.verifyEmail();

            assertThatThrownBy(user::verifyEmail)
                    .isInstanceOf(AlreadyVerifiedException.class);
        }
    }
}
