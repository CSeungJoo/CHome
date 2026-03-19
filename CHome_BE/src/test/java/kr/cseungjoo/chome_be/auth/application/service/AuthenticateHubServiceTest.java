package kr.cseungjoo.chome_be.auth.application.service;

import kr.cseungjoo.chome_be.auth.application.exception.HubAuthenticationFailedException;
import kr.cseungjoo.chome_be.auth.port.in.AuthenticateHubCommand;
import kr.cseungjoo.chome_be.hub.port.out.HubRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthenticateHubServiceTest {

    @InjectMocks
    private AuthenticateHubService authenticateHubService;

    @Mock
    private HubRepositoryPort hubRepositoryPort;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authenticateHubService, "backendUsername", "chome-backend");
    }

    @Test
    @DisplayName("등록된 serialNumber로 인증에 성공한다")
    void success() {
        String serialNumber = "HUB-001";
        AuthenticateHubCommand command = new AuthenticateHubCommand("client-1", serialNumber, "");

        given(hubRepositoryPort.exists(serialNumber)).willReturn(true);

        assertThatCode(() -> authenticateHubService.execute(command))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("등록되지 않은 serialNumber이면 HubAuthenticationFailedException 발생")
    void failWhenHubNotFound() {
        String serialNumber = "UNKNOWN-HUB";
        AuthenticateHubCommand command = new AuthenticateHubCommand("client-1", serialNumber, "");

        given(hubRepositoryPort.exists(serialNumber)).willReturn(false);

        assertThatThrownBy(() -> authenticateHubService.execute(command))
                .isInstanceOf(HubAuthenticationFailedException.class);
    }

    @Test
    @DisplayName("백엔드 클라이언트 username이면 인증을 통과한다")
    void successForBackendClient() {
        AuthenticateHubCommand command = new AuthenticateHubCommand("chome-backend", "chome-backend", "");

        assertThatCode(() -> authenticateHubService.execute(command))
                .doesNotThrowAnyException();
    }
}
