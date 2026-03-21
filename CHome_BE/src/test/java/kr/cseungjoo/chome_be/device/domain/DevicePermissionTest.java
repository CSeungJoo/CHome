package kr.cseungjoo.chome_be.device.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class DevicePermissionTest {

    @Test
    @DisplayName("DevicePermission을 복원한다")
    void restore() {
        DevicePermission permission = DevicePermission.restore(1L, DeviceAction.READ, 10L, 20L);

        assertThat(permission.getId()).isEqualTo(1L);
        assertThat(permission.getAction()).isEqualTo(DeviceAction.READ);
        assertThat(permission.getDeviceId()).isEqualTo(10L);
        assertThat(permission.getUserId()).isEqualTo(20L);
    }

    @Test
    @DisplayName("id가 null이면 IllegalStateException 발생")
    void failWhenIdNull() {
        assertThatThrownBy(() -> DevicePermission.restore(null, DeviceAction.READ, 1L, 2L))
                .isInstanceOf(IllegalStateException.class);
    }
}
