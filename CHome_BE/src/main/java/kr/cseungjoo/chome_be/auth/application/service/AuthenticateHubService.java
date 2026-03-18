package kr.cseungjoo.chome_be.auth.application.service;

import kr.cseungjoo.chome_be.auth.application.exception.HubAuthenticationFailedException;
import kr.cseungjoo.chome_be.auth.port.in.AuthenticateHubCommand;
import kr.cseungjoo.chome_be.auth.port.in.AuthenticateHubUseCase;
import kr.cseungjoo.chome_be.hub.port.out.HubRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticateHubService implements AuthenticateHubUseCase {

    private final HubRepositoryPort hubRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public void execute(AuthenticateHubCommand command) {
        String serialNumber = command.username();

        if (!hubRepositoryPort.exists(serialNumber)) {
            throw new HubAuthenticationFailedException();
        }
    }
}
