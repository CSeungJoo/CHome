package kr.cseungjoo.chome_be.device.application.service;

import kr.cseungjoo.chome_be.device.application.exception.DeviceNotFoundException;
import kr.cseungjoo.chome_be.device.domain.Device;
import kr.cseungjoo.chome_be.device.domain.DeviceAction;
import kr.cseungjoo.chome_be.device.domain.DeviceCommand;
import kr.cseungjoo.chome_be.device.domain.DevicePermission;
import kr.cseungjoo.chome_be.device.port.in.FindDeviceDetailCommand;
import kr.cseungjoo.chome_be.device.port.in.FindDeviceDetailResult;
import kr.cseungjoo.chome_be.device.port.out.DeviceCommandRepositoryPort;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FindDeviceDetailServiceTest {

    @InjectMocks
    private FindDeviceDetailService findDeviceDetailService;

    @Mock
    private DeviceRepositoryPort deviceRepositoryPort;

    @Mock
    private DevicePermissionRepositoryPort devicePermissionRepositoryPort;

    @Mock
    private DeviceCommandRepositoryPort deviceCommandRepositoryPort;

    @Test
    @DisplayName("디바이스 상세 조회 시 명령어 목록이 함께 반환된다")
    void successWithCommands() {
        // given
        long userId = 1L;
        long deviceId = 10L;
        Device device = Device.restore(deviceId, "SN-D001", "조명", "light", "거실 조명", 1L, Instant.now());
        DevicePermission permission = DevicePermission.restore(1L, DeviceAction.READ, deviceId, userId);
        DeviceCommand cmd1 = DeviceCommand.restore(1L, "TURN_ON", "조명을 켭니다", deviceId);
        DeviceCommand cmd2 = DeviceCommand.restore(2L, "TURN_OFF", "조명을 끕니다", deviceId);

        given(deviceRepositoryPort.findById(deviceId)).willReturn(Optional.of(device));
        given(devicePermissionRepositoryPort.findByUserIdAndDeviceId(userId, deviceId))
                .willReturn(List.of(permission));
        given(deviceCommandRepositoryPort.findByDeviceId(deviceId)).willReturn(List.of(cmd1, cmd2));

        // when
        FindDeviceDetailResult result = findDeviceDetailService.execute(
                new FindDeviceDetailCommand(userId, deviceId)
        );

        // then
        assertThat(result.id()).isEqualTo(deviceId);
        assertThat(result.serialNumber()).isEqualTo("SN-D001");
        assertThat(result.name()).isEqualTo("조명");
        assertThat(result.commands()).hasSize(2);
        assertThat(result.commands().get(0).command()).isEqualTo("TURN_ON");
        assertThat(result.commands().get(1).command()).isEqualTo("TURN_OFF");
    }

    @Test
    @DisplayName("명령어가 없는 디바이스도 정상 조회된다")
    void successWithNoCommands() {
        // given
        long userId = 1L;
        long deviceId = 10L;
        Device device = Device.restore(deviceId, "SN-D001", "온도센서", "sensor", "거실 온도", 1L, Instant.now());
        DevicePermission permission = DevicePermission.restore(1L, DeviceAction.READ, deviceId, userId);

        given(deviceRepositoryPort.findById(deviceId)).willReturn(Optional.of(device));
        given(devicePermissionRepositoryPort.findByUserIdAndDeviceId(userId, deviceId))
                .willReturn(List.of(permission));
        given(deviceCommandRepositoryPort.findByDeviceId(deviceId)).willReturn(List.of());

        // when
        FindDeviceDetailResult result = findDeviceDetailService.execute(
                new FindDeviceDetailCommand(userId, deviceId)
        );

        // then
        assertThat(result.id()).isEqualTo(deviceId);
        assertThat(result.commands()).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 디바이스이면 DeviceNotFoundException 발생")
    void failWhenDeviceNotFound() {
        // given
        given(deviceRepositoryPort.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> findDeviceDetailService.execute(
                new FindDeviceDetailCommand(1L, 999L)
        )).isInstanceOf(DeviceNotFoundException.class);
    }

    @Test
    @DisplayName("READ 권한이 없으면 DeviceNotFoundException 발생")
    void failWhenNoReadPermission() {
        // given
        long userId = 1L;
        long deviceId = 10L;
        Device device = Device.restore(deviceId, "SN-D001", "조명", "light", "거실 조명", 1L, Instant.now());

        given(deviceRepositoryPort.findById(deviceId)).willReturn(Optional.of(device));
        given(devicePermissionRepositoryPort.findByUserIdAndDeviceId(userId, deviceId))
                .willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> findDeviceDetailService.execute(
                new FindDeviceDetailCommand(userId, deviceId)
        )).isInstanceOf(DeviceNotFoundException.class);
    }
}
