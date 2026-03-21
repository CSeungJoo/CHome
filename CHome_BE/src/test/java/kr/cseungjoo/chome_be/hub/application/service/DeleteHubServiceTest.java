package kr.cseungjoo.chome_be.hub.application.service;

import kr.cseungjoo.chome_be.hub.application.exception.HubNotFoundException;
import kr.cseungjoo.chome_be.hub.domain.Hub;
import kr.cseungjoo.chome_be.hub.domain.HubAction;
import kr.cseungjoo.chome_be.hub.domain.HubPermission;
import kr.cseungjoo.chome_be.hub.domain.exception.HubPermissionDeniedException;
import kr.cseungjoo.chome_be.hub.port.in.DeleteHubCommand;
import kr.cseungjoo.chome_be.hub.port.in.DeleteHubResult;
import kr.cseungjoo.chome_be.hub.port.out.HubPermissionRepositoryPort;
import kr.cseungjoo.chome_be.hub.port.out.HubRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteHubServiceTest {

    @InjectMocks
    private DeleteHubService deleteHubService;

    @Mock
    private HubRepositoryPort hubRepositoryPort;

    @Mock
    private HubPermissionRepositoryPort hubPermissionRepositoryPort;

    @Test
    @DisplayName("소유자가 허브를 삭제한다")
    void successAsOwner() {
        // given
        Hub hub = Hub.restore(1L, "SN-001", "거실 허브", 1L, Instant.now());
        DeleteHubCommand command = new DeleteHubCommand(1L, 1L);

        given(hubRepositoryPort.findById(1L)).willReturn(Optional.of(hub));
        given(hubPermissionRepositoryPort.findByUserIdAndHubId(1L, 1L)).willReturn(List.of());

        // when
        DeleteHubResult result = deleteHubService.execute(command);

        // then
        assertThat(result.serialNumber()).isEqualTo("SN-001");
        assertThat(result.deletedAt()).isNotNull();
        then(hubRepositoryPort).should().delete(hub);
    }

    @Test
    @DisplayName("DELETE 권한이 있는 유저가 허브를 삭제한다")
    void successWithDeletePermission() {
        // given
        Hub hub = Hub.restore(1L, "SN-001", "거실 허브", 1L, Instant.now());
        HubPermission deletePermission = HubPermission.restore(1L, HubAction.DELETE, 1L, 2L);
        DeleteHubCommand command = new DeleteHubCommand(2L, 1L);

        given(hubRepositoryPort.findById(1L)).willReturn(Optional.of(hub));
        given(hubPermissionRepositoryPort.findByUserIdAndHubId(2L, 1L)).willReturn(List.of(deletePermission));

        // when
        DeleteHubResult result = deleteHubService.execute(command);

        // then
        assertThat(result.serialNumber()).isEqualTo("SN-001");
        then(hubRepositoryPort).should().delete(hub);
    }

    @Test
    @DisplayName("허브를 찾을 수 없으면 HubNotFoundException 발생")
    void failWhenHubNotFound() {
        // given
        DeleteHubCommand command = new DeleteHubCommand(1L, 999L);
        given(hubRepositoryPort.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> deleteHubService.execute(command))
                .isInstanceOf(HubNotFoundException.class);
    }

    @Test
    @DisplayName("권한이 없으면 HubPermissionDeniedException 발생")
    void failWithoutPermission() {
        // given
        Hub hub = Hub.restore(1L, "SN-001", "거실 허브", 1L, Instant.now());
        DeleteHubCommand command = new DeleteHubCommand(2L, 1L);

        given(hubRepositoryPort.findById(1L)).willReturn(Optional.of(hub));
        given(hubPermissionRepositoryPort.findByUserIdAndHubId(2L, 1L)).willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> deleteHubService.execute(command))
                .isInstanceOf(HubPermissionDeniedException.class);
    }
}
