package kr.cseungjoo.chome_be.hub.application.service;

import kr.cseungjoo.chome_be.hub.application.exception.AlreadyExistsHubException;
import kr.cseungjoo.chome_be.hub.domain.Hub;
import kr.cseungjoo.chome_be.hub.port.in.RegisterHubCommand;
import kr.cseungjoo.chome_be.hub.port.in.RegisterHubResult;
import kr.cseungjoo.chome_be.hub.port.out.HubRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterHubServiceTest {

    @InjectMocks
    private RegisterHubService registerHubService;

    @Mock
    private HubRepositoryPort hubRepositoryPort;

    @Test
    @DisplayName("허브 등록에 성공한다")
    void success() {
        // given
        RegisterHubCommand command = new RegisterHubCommand("SN-001", "거실 허브", 1L);

        given(hubRepositoryPort.exists("SN-001")).willReturn(false);
        given(hubRepositoryPort.save(any(Hub.class))).willAnswer(invocation -> {
            Hub hub = invocation.getArgument(0);
            return Hub.restore(1L, hub.getSerialNumber(), hub.getAlias(), hub.getOwnerId(), hub.getCreatedAt());
        });

        // when
        RegisterHubResult result = registerHubService.execute(command);

        // then
        assertThat(result.serialNumber()).isEqualTo("SN-001");
        assertThat(result.alias()).isEqualTo("거실 허브");
        assertThat(result.createdAt()).isNotNull();
    }

    @Test
    @DisplayName("이미 등록된 시리얼 넘버이면 AlreadyExistsHubException 발생")
    void failWhenSerialNumberAlreadyExists() {
        // given
        RegisterHubCommand command = new RegisterHubCommand("SN-001", "거실 허브", 1L);
        given(hubRepositoryPort.exists("SN-001")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> registerHubService.execute(command))
                .isInstanceOf(AlreadyExistsHubException.class);

        then(hubRepositoryPort).should(never()).save(any());
    }
}
