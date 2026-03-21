package kr.cseungjoo.chome_be.hub.application.service;

import kr.cseungjoo.chome_be.hub.application.exception.HubNotFoundException;
import kr.cseungjoo.chome_be.hub.domain.Hub;
import kr.cseungjoo.chome_be.hub.domain.HubAction;
import kr.cseungjoo.chome_be.hub.domain.HubPermission;
import kr.cseungjoo.chome_be.hub.domain.exception.HubPermissionDeniedException;
import kr.cseungjoo.chome_be.hub.port.in.ChangeHubAliasCommand;
import kr.cseungjoo.chome_be.hub.port.in.ChangeHubAliasResult;
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
class ChangeHubAliasServiceTest {

    @InjectMocks
    private ChangeHubAliasService changeHubAliasService;

    @Mock
    private HubRepositoryPort hubRepositoryPort;

    @Mock
    private HubPermissionRepositoryPort hubPermissionRepositoryPort;

    @Test
    @DisplayName("소유자가 허브 별명을 변경한다")
    void successAsOwner() {
        // given
        Hub hub = Hub.restore(1L, "SN-001", "거실 허브", 1L, Instant.now());
        ChangeHubAliasCommand command = new ChangeHubAliasCommand(1L, "안방 허브", 1L);

        given(hubRepositoryPort.findById(1L)).willReturn(Optional.of(hub));
        given(hubPermissionRepositoryPort.findByUserIdAndHubId(1L, 1L)).willReturn(List.of());

        // when
        ChangeHubAliasResult result = changeHubAliasService.execute(command);

        // then
        assertThat(result.alias()).isEqualTo("안방 허브");
        assertThat(result.changedAt()).isNotNull();
        then(hubRepositoryPort).should().save(hub);
    }

    @Test
    @DisplayName("UPDATE 권한이 있는 유저가 별명을 변경한다")
    void successWithUpdatePermission() {
        // given
        Hub hub = Hub.restore(1L, "SN-001", "거실 허브", 1L, Instant.now());
        HubPermission updatePermission = HubPermission.restore(1L, HubAction.UPDATE, 1L, 2L);
        ChangeHubAliasCommand command = new ChangeHubAliasCommand(1L, "안방 허브", 2L);

        given(hubRepositoryPort.findById(1L)).willReturn(Optional.of(hub));
        given(hubPermissionRepositoryPort.findByUserIdAndHubId(2L, 1L)).willReturn(List.of(updatePermission));

        // when
        ChangeHubAliasResult result = changeHubAliasService.execute(command);

        // then
        assertThat(result.alias()).isEqualTo("안방 허브");
    }

    @Test
    @DisplayName("허브를 찾을 수 없으면 HubNotFoundException 발생")
    void failWhenHubNotFound() {
        // given
        ChangeHubAliasCommand command = new ChangeHubAliasCommand(999L, "새 별명", 1L);
        given(hubRepositoryPort.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> changeHubAliasService.execute(command))
                .isInstanceOf(HubNotFoundException.class);
    }

    @Test
    @DisplayName("권한이 없으면 HubPermissionDeniedException 발생")
    void failWithoutPermission() {
        // given
        Hub hub = Hub.restore(1L, "SN-001", "거실 허브", 1L, Instant.now());
        ChangeHubAliasCommand command = new ChangeHubAliasCommand(1L, "안방 허브", 2L);

        given(hubRepositoryPort.findById(1L)).willReturn(Optional.of(hub));
        given(hubPermissionRepositoryPort.findByUserIdAndHubId(2L, 1L)).willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> changeHubAliasService.execute(command))
                .isInstanceOf(HubPermissionDeniedException.class);
    }
}
