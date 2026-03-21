package kr.cseungjoo.chome_be.device.application.service;

import kr.cseungjoo.chome_be.device.domain.Device;
import kr.cseungjoo.chome_be.device.domain.DeviceAction;
import kr.cseungjoo.chome_be.device.domain.DevicePermission;
import kr.cseungjoo.chome_be.device.port.in.FindAccessibleDeviceCommand;
import kr.cseungjoo.chome_be.device.port.in.FindAccessibleDeviceResult;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class FindAccessibleDeviceServiceTest {

    @InjectMocks
    private FindAccessibleDeviceService findAccessibleDeviceService;

    @Mock
    private DeviceRepositoryPort deviceRepositoryPort;

    @Mock
    private DevicePermissionRepositoryPort devicePermissionRepositoryPort;

    @Test
    @DisplayName("READ 권한이 있는 디바이스만 조회된다")
    void findAccessibleDevices() {
        // given
        long hubId = 1L;
        long userId = 2L;
        Device device1 = Device.restore(1L, "SN-D001", "온도센서", "sensor", "거실 온도", hubId, Instant.now());
        Device device2 = Device.restore(2L, "SN-D002", "조명", "light", "안방 조명", hubId, Instant.now());

        DevicePermission readPermission = DevicePermission.restore(1L, DeviceAction.READ, 1L, userId);

        given(deviceRepositoryPort.findByHubId(hubId)).willReturn(List.of(device1, device2));
        given(devicePermissionRepositoryPort.findByUserIdAndDeviceIds(userId, List.of(1L, 2L)))
                .willReturn(List.of(readPermission));

        // when
        FindAccessibleDeviceResult result = findAccessibleDeviceService.execute(
                new FindAccessibleDeviceCommand(hubId, userId));

        // then
        assertThat(result.devices()).hasSize(1);
        assertThat(result.devices().get(0).serialNumber()).isEqualTo("SN-D001");
        assertThat(result.devices().get(0).name()).isEqualTo("온도센서");
    }

    @Test
    @DisplayName("권한이 없으면 빈 목록을 반환한다")
    void emptyWhenNoPermission() {
        // given
        long hubId = 1L;
        long userId = 2L;
        Device device = Device.restore(1L, "SN-D001", "온도센서", "sensor", "거실 온도", hubId, Instant.now());

        given(deviceRepositoryPort.findByHubId(hubId)).willReturn(List.of(device));
        given(devicePermissionRepositoryPort.findByUserIdAndDeviceIds(userId, List.of(1L)))
                .willReturn(List.of());

        // when
        FindAccessibleDeviceResult result = findAccessibleDeviceService.execute(
                new FindAccessibleDeviceCommand(hubId, userId));

        // then
        assertThat(result.devices()).isEmpty();
    }

    @Test
    @DisplayName("허브에 디바이스가 없으면 빈 목록을 반환한다")
    void emptyWhenNoDevices() {
        // given
        long hubId = 1L;
        long userId = 2L;

        given(deviceRepositoryPort.findByHubId(hubId)).willReturn(List.of());
        given(devicePermissionRepositoryPort.findByUserIdAndDeviceIds(userId, List.of()))
                .willReturn(List.of());

        // when
        FindAccessibleDeviceResult result = findAccessibleDeviceService.execute(
                new FindAccessibleDeviceCommand(hubId, userId));

        // then
        assertThat(result.devices()).isEmpty();
    }

    @Test
    @DisplayName("여러 디바이스에 모두 READ 권한이 있으면 전부 조회된다")
    void findAllAccessibleDevices() {
        // given
        long hubId = 1L;
        long userId = 2L;
        Device device1 = Device.restore(1L, "SN-D001", "온도센서", "sensor", "거실 온도", hubId, Instant.now());
        Device device2 = Device.restore(2L, "SN-D002", "조명", "light", "안방 조명", hubId, Instant.now());

        DevicePermission perm1 = DevicePermission.restore(1L, DeviceAction.READ, 1L, userId);
        DevicePermission perm2 = DevicePermission.restore(2L, DeviceAction.READ, 2L, userId);

        given(deviceRepositoryPort.findByHubId(hubId)).willReturn(List.of(device1, device2));
        given(devicePermissionRepositoryPort.findByUserIdAndDeviceIds(userId, List.of(1L, 2L)))
                .willReturn(List.of(perm1, perm2));

        // when
        FindAccessibleDeviceResult result = findAccessibleDeviceService.execute(
                new FindAccessibleDeviceCommand(hubId, userId));

        // then
        assertThat(result.devices()).hasSize(2);
    }
}
