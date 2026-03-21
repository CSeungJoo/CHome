package kr.cseungjoo.chome_be.shared.adapter.web.sse;

import kr.cseungjoo.chome_be.shared.port.out.SseEmitterPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
public class SseEmitterRegistry implements SseEmitterPort {

    private static final long TIMEOUT = 60 * 60 * 1000L;

    private final Map<Long, Set<SseEmitter>> hubEmitters = new ConcurrentHashMap<>();

    public SseEmitter connect(long hubId, long userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        Set<SseEmitter> emitters = hubEmitters.computeIfAbsent(hubId, k -> new CopyOnWriteArraySet<>());

        emitter.onCompletion(() -> {
            emitters.remove(emitter);
            if (emitters.isEmpty()) hubEmitters.remove(hubId);
            log.info("SSE 연결 종료: hubId={}, userId={}", hubId, userId);
        });
        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            if (emitters.isEmpty()) hubEmitters.remove(hubId);
            log.info("SSE 연결 타임아웃: hubId={}, userId={}", hubId, userId);
        });
        emitter.onError(e -> {
            emitters.remove(emitter);
            if (emitters.isEmpty()) hubEmitters.remove(hubId);
            log.warn("SSE 연결 에러: hubId={}, userId={}", hubId, userId, e);
        });

        emitters.add(emitter);
        log.info("SSE 연결 성공: hubId={}, userId={}", hubId, userId);

        sendConnectEvent(emitter);

        return emitter;
    }

    @Override
    public void sendToHub(long hubId, String eventName, Object data) {
        Set<SseEmitter> emitters = hubEmitters.get(hubId);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(data));
            } catch (IOException e) {
                emitters.remove(emitter);
                log.warn("SSE 전송 실패, 연결 제거: hubId={}", hubId);
            }
        }
    }

    private void sendConnectEvent(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("연결 성공"));
        } catch (IOException e) {
            log.warn("SSE 초기 이벤트 전송 실패", e);
        }
    }
}
