package kr.cseungjoo.chome_be.device.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class DeviceCommandTest {

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("디바이스 명령어를 생성한다")
        void success() {
            DeviceCommand command = DeviceCommand.create("TURN_ON", "조명을 켭니다", 1L);

            assertThat(command.getId()).isNull();
            assertThat(command.getCommand()).isEqualTo("TURN_ON");
            assertThat(command.getDescription()).isEqualTo("조명을 켭니다");
            assertThat(command.getDeviceId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("restore")
    class Restore {

        @Test
        @DisplayName("디바이스 명령어를 복원한다")
        void success() {
            DeviceCommand command = DeviceCommand.restore(1L, "TURN_OFF", "조명을 끕니다", 10L);

            assertThat(command.getId()).isEqualTo(1L);
            assertThat(command.getCommand()).isEqualTo("TURN_OFF");
            assertThat(command.getDescription()).isEqualTo("조명을 끕니다");
            assertThat(command.getDeviceId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("id가 null이면 IllegalStateException 발생")
        void failWhenIdNull() {
            assertThatThrownBy(() -> DeviceCommand.restore(null, "TURN_ON", "조명을 켭니다", 1L))
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}
