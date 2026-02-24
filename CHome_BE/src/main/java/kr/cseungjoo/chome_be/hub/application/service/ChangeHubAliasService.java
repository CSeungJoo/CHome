package kr.cseungjoo.chome_be.hub.application.service;

import kr.cseungjoo.chome_be.hub.application.command.ChangeHubAliasCommand;
import kr.cseungjoo.chome_be.hub.application.exception.HubNotFoundException;
import kr.cseungjoo.chome_be.hub.application.result.ChangeHubAliasResult;
import kr.cseungjoo.chome_be.hub.domain.Hub;
import kr.cseungjoo.chome_be.hub.domain.HubPermission;
import kr.cseungjoo.chome_be.hub.port.in.ChangeHubAliasUseCase;
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
public class ChangeHubAliasService implements ChangeHubAliasUseCase {

    private final HubRepositoryPort hubRepositoryPort;
    private final HubPermissionRepositoryPort hubPermissionRepositoryPort;

    @Override
    public ChangeHubAliasResult execute(ChangeHubAliasCommand command) {
        Hub hub = hubRepositoryPort.findById(command.hubId()).orElseThrow(
                () -> new HubNotFoundException("허브를 찾을 수 없습니다.")
        );

        List<HubPermission> permissions = hubPermissionRepositoryPort.findByUserIdAndHubId(command.userId(), command.hubId());

        hub.assertUpdatableBy(command.userId(), permissions);

        hub.renameAlias(command.alias());

        hubRepositoryPort.save(hub);

        return new ChangeHubAliasResult(
                hub.getAlias(),
                Instant.now()
        );
    }
}
