package kr.cseungjoo.chome_be.user.application.service;

import kr.cseungjoo.chome_be.user.application.exception.UserNotFoundException;
import kr.cseungjoo.chome_be.user.application.port.out.EmailVerificationTokenPort;
import kr.cseungjoo.chome_be.user.port.out.UserRepositoryPort;
import kr.cseungjoo.chome_be.user.domain.Role;
import kr.cseungjoo.chome_be.user.domain.User;
import kr.cseungjoo.chome_be.user.domain.exception.AlreadyVerifiedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class VerifyEmailServiceTest {

    @InjectMocks
    private VerifyEmailService verifyEmailService;

    @Mock
    private EmailVerificationTokenPort emailVerificationTokenPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Test
    @DisplayName("이메일 인증에 성공한다")
    void success() {
        // given
        String token = "valid-token";
        User user = User.restore(1L, "홍길동", "test@example.com", "pw", Role.USER, null, Instant.now(), null);

        given(emailVerificationTokenPort.resolve(token)).willReturn(1L);
        given(userRepositoryPort.findById(1L)).willReturn(Optional.of(user));

        // when
        verifyEmailService.execute(token);

        // then
        assertThat(user.isEmailVerified()).isTrue();
        then(userRepositoryPort).should().save(user);
    }

    @Test
    @DisplayName("존재하지 않는 유저이면 UserNotFoundException 발생")
    void failWhenUserNotFound() {
        // given
        given(emailVerificationTokenPort.resolve("token")).willReturn(999L);
        given(userRepositoryPort.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> verifyEmailService.execute("token"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("이미 인증된 이메일이면 AlreadyVerifiedException 발생")
    void failWhenAlreadyVerified() {
        // given
        Instant now = Instant.now();
        User user = User.restore(1L, "홍길동", "test@example.com", "pw", Role.USER, now, now, null);

        given(emailVerificationTokenPort.resolve("token")).willReturn(1L);
        given(userRepositoryPort.findById(1L)).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> verifyEmailService.execute("token"))
                .isInstanceOf(AlreadyVerifiedException.class);
    }
}
