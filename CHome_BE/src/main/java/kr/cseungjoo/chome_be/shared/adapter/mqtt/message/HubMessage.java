package kr.cseungjoo.chome_be.shared.adapter.mqtt.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HubMessage {
    private MessageKind kind;
    private String type;
    private String requestId;
    private long timestamp;
    private Map<String, Object> payload;

    public static HubMessage command(String type, Map<String, Object> payload) {
        return new HubMessage(
                MessageKind.COMMAND,
                type,
                UUID.randomUUID().toString(),
                Instant.now().getEpochSecond(),
                payload
        );
    }

    public static HubMessage command(String type) {
        return command(type, Map.of());
    }
}
