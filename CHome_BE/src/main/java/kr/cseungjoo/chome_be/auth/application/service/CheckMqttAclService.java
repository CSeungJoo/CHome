package kr.cseungjoo.chome_be.auth.application.service;

import kr.cseungjoo.chome_be.auth.application.exception.HubAuthenticationFailedException;
import kr.cseungjoo.chome_be.auth.port.in.CheckMqttAclCommand;
import kr.cseungjoo.chome_be.auth.port.in.CheckMqttAclUseCase;
import org.springframework.stereotype.Service;

@Service
public class CheckMqttAclService implements CheckMqttAclUseCase {

    @Override
    public void execute(CheckMqttAclCommand command) {
        // topic: hub/{serialNumber}/command, hub/{serialNumber}/result, hub/{serialNumber}/event
        // username = serialNumber
        String[] parts = command.topic().split("/");

        if (parts.length != 3 || !"hub".equals(parts[0])) {
            throw new HubAuthenticationFailedException();
        }

        String topicSerialNumber = parts[1];
        if (!command.username().equals(topicSerialNumber)) {
            throw new HubAuthenticationFailedException();
        }
    }
}
