package kr.cseungjoo.chome_be.auth.application.service;

import kr.cseungjoo.chome_be.auth.application.exception.InvalidRefreshTokenException;
import kr.cseungjoo.chome_be.auth.port.in.RefreshCommand;
import kr.cseungjoo.chome_be.auth.port.in.RefreshResult;
import kr.cseungjoo.chome_be.shared.port.out.RefreshTokenPort;
import kr.cseungjoo.chome_be.shared.port.out.TokenProviderPort;
import kr.cseungjoo.chome_be.user.application.exception.UserNotFoundException;
import kr.cseungjoo.chome_be.user.application.port.out.UserRepositoryPort;
import kr.cseungjoo.chome_be.user.domain.Role;
import kr.cseungjoo.chome_be.user.domain.User;
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
class RefreshServiceTest {

    @InjectMocks
    private RefreshService refreshService;

    @Mock
    private RefreshTokenPort refreshTokenPort;

    @Mock
    private TokenProviderPort tokenProviderPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Test
    @DisplayName("토큰 갱신에 성공한다")
    void success() {
        // given
        Instant now = Instant.now();
        User user = User.restore(1L, "홍길동", "test@example.com", "pw", Role.USER, now, now, null);
        RefreshCommand command = new RefreshCommand("old-refresh-token");

        given(refreshTokenPort.resolve("old-refresh-token")).willReturn(Optional.of(1L));
        given(userRepositoryPort.findById(1L)).willReturn(Optional.of(user));
        given(tokenProviderPort.issue(1L, Role.USER)).willReturn("new-access-token");
        given(refreshTokenPort.issue(1L)).willReturn("new-refresh-token");

        // when
        RefreshResult result = refreshService.execute(command);

        // then
        assertThat(result.accessToken()).isEqualTo("new-access-token");
        assertThat(result.refreshToken()).isEqualTo("new-refresh-token");
        then(refreshTokenPort).should().revoke("old-refresh-token");
    }

    @Test
    @DisplayName("유효하지 않은 리프레시 토큰이면 InvalidRefreshTokenException 발생")
    void failWhenInvalidRefreshToken() {
        // given
        RefreshCommand command = new RefreshCommand("invalid-token");
        given(refreshTokenPort.resolve("invalid-token")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> refreshService.execute(command))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    @DisplayName("유저를 찾을 수 없으면 UserNotFoundException 발생")
    void failWhenUserNotFound() {
        // given
        RefreshCommand command = new RefreshCommand("valid-token");
        given(refreshTokenPort.resolve("valid-token")).willReturn(Optional.of(999L));
        given(userRepositoryPort.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> refreshService.execute(command))
                .isInstanceOf(UserNotFoundException.class);
    }
}
