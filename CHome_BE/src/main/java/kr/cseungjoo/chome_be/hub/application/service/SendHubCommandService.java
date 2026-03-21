package kr.cseungjoo.chome_be.hub.application.service;

import kr.cseungjoo.chome_be.hub.application.exception.HubNotFoundException;
import kr.cseungjoo.chome_be.hub.domain.Hub;
import kr.cseungjoo.chome_be.hub.domain.HubPermission;
import kr.cseungjoo.chome_be.hub.port.in.SendHubCommandCommand;
import kr.cseungjoo.chome_be.hub.port.in.SendHubCommandResult;
import kr.cseungjoo.chome_be.hub.port.in.SendHubCommandUseCase;
import kr.cseungjoo.chome_be.hub.port.out.HubPermissionRepositoryPort;
import kr.cseungjoo.chome_be.hub.port.out.HubRepositoryPort;
import kr.cseungjoo.chome_be.shared.adapter.mqtt.message.HubMessage;
import kr.cseungjoo.chome_be.shared.port.out.MqttPublishPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SendHubCommandService implements SendHubCommandUseCase {

    private final HubRepositoryPort hubRepositoryPort;
    private final HubPermissionRepositoryPort hubPermissionRepositoryPort;
    private final MqttPublishPort mqttPublishPort;

    @Override
    @Transactional(readOnly = true)
    public SendHubCommandResult execute(SendHubCommandCommand command) {
        Hub hub = hubRepositoryPort.findById(command.hubId())
                .orElseThrow(() -> new HubNotFoundException("허브를 찾을 수 없습니다."));

        List<HubPermission> permissions = hubPermissionRepositoryPort
                .findByUserIdAndHubId(command.userId(), command.hubId());

        hub.assertUpdatableBy(command.userId(), permissions);

        HubMessage hubMessage = HubMessage.command(command.type(), command.payload());
        String topic = "hub/" + hub.getSerialNumber() + "/command";

        mqttPublishPort.publish(topic, hubMessage);

        return new SendHubCommandResult(hubMessage.getRequestId(), hubMessage.getType());
    }
}
