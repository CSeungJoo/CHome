package kr.cseungjoo.chome_be.auth.application.service;

import kr.cseungjoo.chome_be.auth.application.exception.HubAuthenticationFailedException;
import kr.cseungjoo.chome_be.auth.port.in.CheckMqttAclCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CheckMqttAclServiceTest {

    private final CheckMqttAclService checkMqttAclService = new CheckMqttAclService();

    @Test
    @DisplayName("자기 허브의 command 토픽에 접근할 수 있다")
    void allowOwnCommandTopic() {
        CheckMqttAclCommand command = new CheckMqttAclCommand("HUB-001", "hub/HUB-001/command", 1);

        assertThatCode(() -> checkMqttAclService.execute(command))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("자기 허브의 result 토픽에 접근할 수 있다")
    void allowOwnResultTopic() {
        CheckMqttAclCommand command = new CheckMqttAclCommand("HUB-001", "hub/HUB-001/result", 2);

        assertThatCode(() -> checkMqttAclService.execute(command))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("자기 허브의 event 토픽에 접근할 수 있다")
    void allowOwnEventTopic() {
        CheckMqttAclCommand command = new CheckMqttAclCommand("HUB-001", "hub/HUB-001/event", 2);

        assertThatCode(() -> checkMqttAclService.execute(command))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("다른 허브의 토픽에 접근할 수 없다")
    void denyOtherHubTopic() {
        CheckMqttAclCommand command = new CheckMqttAclCommand("HUB-001", "hub/HUB-002/command", 1);

        assertThatThrownBy(() -> checkMqttAclService.execute(command))
                .isInstanceOf(HubAuthenticationFailedException.class);
    }

    @Test
    @DisplayName("잘못된 토픽 형식이면 거부한다")
    void denyInvalidTopicFormat() {
        CheckMqttAclCommand command = new CheckMqttAclCommand("HUB-001", "invalid/topic", 1);

        assertThatThrownBy(() -> checkMqttAclService.execute(command))
                .isInstanceOf(HubAuthenticationFailedException.class);
    }

    @Test
    @DisplayName("hub 접두사가 아닌 토픽이면 거부한다")
    void denyNonHubPrefix() {
        CheckMqttAclCommand command = new CheckMqttAclCommand("HUB-001", "device/HUB-001/command", 1);

        assertThatThrownBy(() -> checkMqttAclService.execute(command))
                .isInstanceOf(HubAuthenticationFailedException.class);
    }
}
