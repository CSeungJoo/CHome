package kr.cseungjoo.chome_be.device.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class DeviceTest {

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("디바이스를 생성한다")
        void success() {
            Device device = Device.create("SN-D001", "온도센서", "sensor", "거실 온도", 1L);

            assertThat(device.getId()).isNull();
            assertThat(device.getSerialNumber()).isEqualTo("SN-D001");
            assertThat(device.getName()).isEqualTo("온도센서");
            assertThat(device.getType()).isEqualTo("sensor");
            assertThat(device.getAlias()).isEqualTo("거실 온도");
            assertThat(device.getHubId()).isEqualTo(1L);
            assertThat(device.getCreatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("restore")
    class Restore {

        @Test
        @DisplayName("디바이스를 복원한다")
        void success() {
            Instant now = Instant.now();
            Device device = Device.restore(1L, "SN-D001", "온도센서", "sensor", "거실 온도", 1L, now);

            assertThat(device.getId()).isEqualTo(1L);
            assertThat(device.getCreatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("id가 null이면 IllegalStateException 발생")
        void failWhenIdNull() {
            assertThatThrownBy(() -> Device.restore(null, "SN-D001", "온도센서", "sensor", "거실 온도", 1L, Instant.now()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("createdAt이 null이면 IllegalStateException 발생")
        void failWhenCreatedAtNull() {
            assertThatThrownBy(() -> Device.restore(1L, "SN-D001", "온도센서", "sensor", "거실 온도", 1L, null))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("canReadBy")
    class CanReadBy {

        @Test
        @DisplayName("READ 권한이 있으면 읽기 가능")
        void canReadWithPermission() {
            Device device = Device.restore(1L, "SN-D001", "온도센서", "sensor", "거실 온도", 1L, Instant.now());
            DevicePermission readPermission = DevicePermission.restore(1L, DeviceAction.READ, 1L, 2L);

            assertThat(device.canReadBy(2L, List.of(readPermission))).isTrue();
        }

        @Test
        @DisplayName("READ 권한이 없으면 읽기 불가")
        void cannotReadWithoutPermission() {
            Device device = Device.restore(1L, "SN-D001", "온도센서", "sensor", "거실 온도", 1L, Instant.now());

            assertThat(device.canReadBy(2L, List.of())).isFalse();
        }

        @Test
        @DisplayName("다른 유저의 READ 권한으로는 읽기 불가")
        void cannotReadWithOtherUsersPermission() {
            Device device = Device.restore(1L, "SN-D001", "온도센서", "sensor", "거실 온도", 1L, Instant.now());
            DevicePermission otherPermission = DevicePermission.restore(1L, DeviceAction.READ, 1L, 3L);

            assertThat(device.canReadBy(2L, List.of(otherPermission))).isFalse();
        }

        @Test
        @DisplayName("READ가 아닌 다른 권한으로는 읽기 불가")
        void cannotReadWithNonReadPermission() {
            Device device = Device.restore(1L, "SN-D001", "온도센서", "sensor", "거실 온도", 1L, Instant.now());
            DevicePermission updatePermission = DevicePermission.restore(1L, DeviceAction.UPDATE, 1L, 2L);

            assertThat(device.canReadBy(2L, List.of(updatePermission))).isFalse();
        }
    }
}
