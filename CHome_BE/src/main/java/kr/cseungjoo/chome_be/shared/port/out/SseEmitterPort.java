package kr.cseungjoo.chome_be.shared.port.out;

public interface SseEmitterPort {
    void sendToHub(long hubId, String eventName, Object data);
}
