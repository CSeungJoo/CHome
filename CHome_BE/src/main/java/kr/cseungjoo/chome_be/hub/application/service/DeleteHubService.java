package kr.cseungjoo.chome_be.hub.application.service;

import kr.cseungjoo.chome_be.hub.port.in.DeleteHubCommand;
import kr.cseungjoo.chome_be.hub.application.exception.HubNotFoundException;
import kr.cseungjoo.chome_be.hub.port.in.DeleteHubResult;
import kr.cseungjoo.chome_be.hub.domain.Hub;
import kr.cseungjoo.chome_be.hub.domain.HubPermission;
import kr.cseungjoo.chome_be.hub.port.in.DeleteHubUseCase;
import kr.cseungjoo.chome_be.hub.port.out.HubPermissionRepositoryPort;
import kr.cseungjoo.chome_be.hub.port.out.HubRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteHubService implements DeleteHubUseCase {

    private final HubRepositoryPort hubRepositoryPort;
    private final HubPermissionRepositoryPort hubPermissionRepositoryPort;
    @Override
    public DeleteHubResult execute(DeleteHubCommand command) {
        Hub hub = hubRepositoryPort.findById(command.hubId()).orElseThrow(
                () -> new HubNotFoundException("이미 삭제되었거나 존재하지 않는 허브입니다.")
        );

        List<HubPermission> hubPermissions = hubPermissionRepositoryPort.findByUserIdAndHubId(command.userId(), command.hubId());

        hub.assertDeletableBy(command.userId(), hubPermissions);

        hubRepositoryPort.delete(hub);

        return new DeleteHubResult(hub.getSerialNumber(), Instant.now());
    }
}
