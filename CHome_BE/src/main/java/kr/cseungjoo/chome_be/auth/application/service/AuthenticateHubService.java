package kr.cseungjoo.chome_be.auth.application.service;

import kr.cseungjoo.chome_be.auth.application.exception.HubAuthenticationFailedException;
import kr.cseungjoo.chome_be.auth.port.in.AuthenticateHubCommand;
import kr.cseungjoo.chome_be.auth.port.in.AuthenticateHubUseCase;
import kr.cseungjoo.chome_be.hub.port.out.HubRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticateHubService implements AuthenticateHubUseCase {

    private final HubRepositoryPort hubRepositoryPort;

    @Value("${app.mqtt.username:}")
    private String backendUsername;

    @Override
    @Transactional(readOnly = true)
    public void execute(AuthenticateHubCommand command) {
        String username = command.username();

        if (backendUsername != null && !backendUsername.isBlank() && backendUsername.equals(username)) {
            return;
        }

        if (!hubRepositoryPort.exists(username)) {
            throw new HubAuthenticationFailedException();
        }
    }
}
