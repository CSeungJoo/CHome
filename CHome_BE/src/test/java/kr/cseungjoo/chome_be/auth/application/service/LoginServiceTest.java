package kr.cseungjoo.chome_be.auth.application.service;

import kr.cseungjoo.chome_be.auth.application.exception.AuthenticationFailedException;
import kr.cseungjoo.chome_be.auth.application.exception.EmailNotVerifiedException;
import kr.cseungjoo.chome_be.auth.port.in.LoginCommand;
import kr.cseungjoo.chome_be.auth.port.in.LoginResult;
import kr.cseungjoo.chome_be.shared.port.out.RefreshTokenPort;
import kr.cseungjoo.chome_be.shared.port.out.TokenProviderPort;
import kr.cseungjoo.chome_be.user.port.out.UserRepositoryPort;
import kr.cseungjoo.chome_be.user.domain.Role;
import kr.cseungjoo.chome_be.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @InjectMocks
    private LoginService loginService;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private TokenProviderPort tokenProviderPort;

    @Mock
    private RefreshTokenPort refreshTokenPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("로그인에 성공한다")
    void success() {
        // given
        Instant now = Instant.now();
        User user = User.restore(1L, "홍길동", "test@example.com", "encoded-pw", Role.USER, now, now, null);
        LoginCommand command = new LoginCommand("test@example.com", "password123");

        given(userRepositoryPort.findByEmail("test@example.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("password123", "encoded-pw")).willReturn(true);
        given(tokenProviderPort.issue(1L, Role.USER)).willReturn("access-token");
        given(refreshTokenPort.issue(1L)).willReturn("refresh-token");

        // when
        LoginResult result = loginService.execute(command);

        // then
        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
        assertThat(result.userId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("존재하지 않는 이메일이면 AuthenticationFailedException 발생")
    void failWhenUserNotFound() {
        // given
        LoginCommand command = new LoginCommand("unknown@example.com", "password123");
        given(userRepositoryPort.findByEmail("unknown@example.com")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> loginService.execute(command))
                .isInstanceOf(AuthenticationFailedException.class);
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 AuthenticationFailedException 발생")
    void failWhenPasswordMismatch() {
        // given
        Instant now = Instant.now();
        User user = User.restore(1L, "홍길동", "test@example.com", "encoded-pw", Role.USER, now, now, null);
        LoginCommand command = new LoginCommand("test@example.com", "wrong-password");

        given(userRepositoryPort.findByEmail("test@example.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrong-password", "encoded-pw")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> loginService.execute(command))
                .isInstanceOf(AuthenticationFailedException.class);
    }

    @Test
    @DisplayName("이메일 미인증이면 EmailNotVerifiedException 발생")
    void failWhenEmailNotVerified() {
        // given
        User user = User.restore(1L, "홍길동", "test@example.com", "encoded-pw", Role.USER, null, Instant.now(), null);
        LoginCommand command = new LoginCommand("test@example.com", "password123");

        given(userRepositoryPort.findByEmail("test@example.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("password123", "encoded-pw")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> loginService.execute(command))
                .isInstanceOf(EmailNotVerifiedException.class);
    }
}
