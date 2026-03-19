package kr.cseungjoo.chome_be.shared.adapter.mqtt.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class HubMessageTest {

    @Nested
    @DisplayName("command with payload")
    class CommandWithPayload {

        @Test
        @DisplayName("COMMAND 메시지를 생성한다")
        void success() {
            Map<String, Object> payload = Map.of("deviceId", "AA:BB:CC");

            HubMessage message = HubMessage.command("BLE_CONNECT", payload);

            assertThat(message.getKind()).isEqualTo(MessageKind.COMMAND);
            assertThat(message.getType()).isEqualTo("BLE_CONNECT");
            assertThat(message.getRequestId()).isNotBlank();
            assertThat(message.getTimestamp()).isGreaterThan(0);
            assertThat(message.getPayload()).containsEntry("deviceId", "AA:BB:CC");
        }

        @Test
        @DisplayName("requestId는 매 호출마다 다르다")
        void uniqueRequestId() {
            HubMessage msg1 = HubMessage.command("BLE_CONNECT", Map.of());
            HubMessage msg2 = HubMessage.command("BLE_CONNECT", Map.of());

            assertThat(msg1.getRequestId()).isNotEqualTo(msg2.getRequestId());
        }

        @Test
        @DisplayName("timestamp는 현재 시각 기준이다")
        void timestampIsNow() {
            long before = Instant.now().getEpochSecond();
            HubMessage message = HubMessage.command("BLE_CONNECT", Map.of());
            long after = Instant.now().getEpochSecond();

            assertThat(message.getTimestamp()).isBetween(before, after);
        }
    }

    @Nested
    @DisplayName("command without payload")
    class CommandWithoutPayload {

        @Test
        @DisplayName("payload 없이 COMMAND 메시지를 생성한다")
        void success() {
            HubMessage message = HubMessage.command("STATUS_CHECK");

            assertThat(message.getKind()).isEqualTo(MessageKind.COMMAND);
            assertThat(message.getType()).isEqualTo("STATUS_CHECK");
            assertThat(message.getPayload()).isEmpty();
        }
    }
}
