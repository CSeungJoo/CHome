package kr.cseungjoo.chome_be.hub.application.service;

import kr.cseungjoo.chome_be.hub.port.in.RegisterHubCommand;
import kr.cseungjoo.chome_be.hub.application.exception.AlreadyExistsHubException;
import kr.cseungjoo.chome_be.hub.port.in.RegisterHubUseCase;
import kr.cseungjoo.chome_be.hub.port.out.HubRepositoryPort;
import kr.cseungjoo.chome_be.hub.port.in.RegisterHubResult;
import kr.cseungjoo.chome_be.hub.domain.Hub;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterHubService implements RegisterHubUseCase {

    private final HubRepositoryPort hubRepositoryPort;

    @Override
    @Transactional
    public RegisterHubResult execute(RegisterHubCommand command) {
        if (hubRepositoryPort.exists(command.serialNumber())) {
            throw new AlreadyExistsHubException("이미 등록된 허브입니다.");
        }
        Hub hub = Hub.create(command.serialNumber(), command.alias(), command.ownerId());

        hub = hubRepositoryPort.save(hub);

        return new RegisterHubResult(hub.getSerialNumber(), hub.getAlias(), hub.getCreatedAt());
    }
}
