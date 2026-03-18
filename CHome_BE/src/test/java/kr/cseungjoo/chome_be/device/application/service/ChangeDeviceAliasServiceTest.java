package kr.cseungjoo.chome_be.device.application.service;

import kr.cseungjoo.chome_be.device.application.exception.DeviceNotFoundException;
import kr.cseungjoo.chome_be.device.domain.Device;
import kr.cseungjoo.chome_be.device.domain.DeviceAction;
import kr.cseungjoo.chome_be.device.domain.DevicePermission;
import kr.cseungjoo.chome_be.device.domain.exception.DevicePermissionDeniedException;
import kr.cseungjoo.chome_be.device.port.in.ChangeDeviceAliasCommand;
import kr.cseungjoo.chome_be.device.port.in.ChangeDeviceAliasResult;
import kr.cseungjoo.chome_be.device.port.out.DevicePermissionRepositoryPort;
import kr.cseungjoo.chome_be.device.port.out.DeviceRepositoryPort;
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
class ChangeDeviceAliasServiceTest {

    @InjectMocks
    private ChangeDeviceAliasService changeDeviceAliasService;

    @Mock
    private DeviceRepositoryPort deviceRepositoryPort;

    @Mock
    private DevicePermissionRepositoryPort devicePermissionRepositoryPort;

    @Test
    @DisplayName("UPDATE 권한이 있는 유저가 디바이스 별명을 변경한다")
    void successWithUpdatePermission() {
        // given
        Device device = Device.restore(1L, "SN-D001", "온도센서", "sensor", "거실 온도", 1L, Instant.now());
        DevicePermission updatePermission = DevicePermission.restore(1L, DeviceAction.UPDATE, 1L, 2L);
        ChangeDeviceAliasCommand command = new ChangeDeviceAliasCommand(1L, "안방 온도", 2L);

        given(deviceRepositoryPort.findById(1L)).willReturn(Optional.of(device));
        given(devicePermissionRepositoryPort.findByUserIdAndDeviceId(2L, 1L)).willReturn(List.of(updatePermission));

        // when
        ChangeDeviceAliasResult result = changeDeviceAliasService.execute(command);

        // then
        assertThat(result.alias()).isEqualTo("안방 온도");
        assertThat(result.changedAt()).isNotNull();
        then(deviceRepositoryPort).should().save(device);
    }

    @Test
    @DisplayName("디바이스를 찾을 수 없으면 DeviceNotFoundException 발생")
    void failWhenDeviceNotFound() {
        // given
        ChangeDeviceAliasCommand command = new ChangeDeviceAliasCommand(999L, "새 별명", 1L);
        given(deviceRepositoryPort.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> changeDeviceAliasService.execute(command))
                .isInstanceOf(DeviceNotFoundException.class);
    }

    @Test
    @DisplayName("권한이 없으면 DevicePermissionDeniedException 발생")
    void failWithoutPermission() {
        // given
        Device device = Device.restore(1L, "SN-D001", "온도센서", "sensor", "거실 온도", 1L, Instant.now());
        ChangeDeviceAliasCommand command = new ChangeDeviceAliasCommand(1L, "안방 온도", 2L);

        given(deviceRepositoryPort.findById(1L)).willReturn(Optional.of(device));
        given(devicePermissionRepositoryPort.findByUserIdAndDeviceId(2L, 1L)).willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> changeDeviceAliasService.execute(command))
                .isInstanceOf(DevicePermissionDeniedException.class);
    }
}
