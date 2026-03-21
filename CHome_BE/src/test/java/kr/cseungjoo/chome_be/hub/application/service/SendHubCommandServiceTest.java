package kr.cseungjoo.chome_be.hub.application.service;

import kr.cseungjoo.chome_be.hub.application.exception.HubNotFoundException;
import kr.cseungjoo.chome_be.hub.domain.Hub;
import kr.cseungjoo.chome_be.hub.domain.HubPermission;
import kr.cseungjoo.chome_be.hub.domain.exception.HubPermissionDeniedException;
import kr.cseungjoo.chome_be.hub.port.in.SendHubCommandCommand;
import kr.cseungjoo.chome_be.hub.port.in.SendHubCommandResult;
import kr.cseungjoo.chome_be.hub.port.out.HubPermissionRepositoryPort;
import kr.cseungjoo.chome_be.hub.port.out.HubRepositoryPort;
import kr.cseungjoo.chome_be.shared.adapter.mqtt.message.HubMessage;
import kr.cseungjoo.chome_be.shared.port.out.MqttPublishPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SendHubCommandServiceTest {

    @InjectMocks
    private SendHubCommandService sendHubCommandService;

    @Mock
    private HubRepositoryPort hubRepositoryPort;

    @Mock
    private HubPermissionRepositoryPort hubPermissionRepositoryPort;

    @Mock
    private MqttPublishPort mqttPublishPort;

    @Test
    @DisplayName("허브 소유자가 커맨드를 전송한다")
    void successByOwner() {
        // given
        long userId = 1L;
        long hubId = 10L;
        Hub hub = Hub.restore(hubId, "HUB-001", "거실 허브", userId, Instant.now());

        given(hubRepositoryPort.findById(hubId)).willReturn(Optional.of(hub));
        given(hubPermissionRepositoryPort.findByUserIdAndHubId(userId, hubId)).willReturn(List.of());

        SendHubCommandCommand command = new SendHubCommandCommand(
                userId, hubId, "BLE_CONNECT", Map.of("deviceId", "AA:BB:CC")
        );

        // when
        SendHubCommandResult result = sendHubCommandService.execute(command);

        // then
        assertThat(result.type()).isEqualTo("BLE_CONNECT");
        assertThat(result.requestId()).isNotBlank();

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HubMessage> messageCaptor = ArgumentCaptor.forClass(HubMessage.class);
        verify(mqttPublishPort).publish(topicCaptor.capture(), messageCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo("hub/HUB-001/command");
        assertThat(messageCaptor.getValue().getType()).isEqualTo("BLE_CONNECT");
    }

    @Test
    @DisplayName("존재하지 않는 허브이면 HubNotFoundException 발생")
    void failWhenHubNotFound() {
        // given
        given(hubRepositoryPort.findById(999L)).willReturn(Optional.empty());

        SendHubCommandCommand command = new SendHubCommandCommand(1L, 999L, "BLE_CONNECT", Map.of());

        // when & then
        assertThatThrownBy(() -> sendHubCommandService.execute(command))
                .isInstanceOf(HubNotFoundException.class);
    }

    @Test
    @DisplayName("권한이 없으면 HubPermissionDeniedException 발생")
    void failWhenNoPermission() {
        // given
        long ownerId = 1L;
        long otherUserId = 2L;
        long hubId = 10L;
        Hub hub = Hub.restore(hubId, "HUB-001", "거실 허브", ownerId, Instant.now());

        given(hubRepositoryPort.findById(hubId)).willReturn(Optional.of(hub));
        given(hubPermissionRepositoryPort.findByUserIdAndHubId(otherUserId, hubId)).willReturn(List.of());

        SendHubCommandCommand command = new SendHubCommandCommand(
                otherUserId, hubId, "BLE_CONNECT", Map.of()
        );

        // when & then
        assertThatThrownBy(() -> sendHubCommandService.execute(command))
                .isInstanceOf(HubPermissionDeniedException.class);
    }
}
