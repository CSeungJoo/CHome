package kr.cseungjoo.chome_be.shared.adapter.web.sse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.assertj.core.api.Assertions.assertThat;

class SseEmitterRegistryTest {

    private final SseEmitterRegistry registry = new SseEmitterRegistry();

    @Test
    @DisplayName("허브별 SSE 연결을 생성한다")
    void connect() {
        SseEmitter emitter = registry.connect(1L, 100L);

        assertThat(emitter).isNotNull();
    }

    @Test
    @DisplayName("같은 허브에 여러 사용자가 연결할 수 있다")
    void multipleUsersPerHub() {
        SseEmitter emitter1 = registry.connect(1L, 100L);
        SseEmitter emitter2 = registry.connect(1L, 200L);

        assertThat(emitter1).isNotSameAs(emitter2);
    }

    @Test
    @DisplayName("연결이 없는 허브에 전송해도 예외가 발생하지 않는다")
    void sendToDisconnectedHub() {
        registry.sendToHub(999L, "test", "data");
    }
}
