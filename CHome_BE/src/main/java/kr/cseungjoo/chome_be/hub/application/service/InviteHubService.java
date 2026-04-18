package kr.cseungjoo.chome_be.hub.application.service;

import kr.cseungjoo.chome_be.hub.application.exception.HubNotFoundException;
import kr.cseungjoo.chome_be.hub.domain.Hub;
import kr.cseungjoo.chome_be.hub.domain.HubAction;
import kr.cseungjoo.chome_be.hub.domain.HubPermission;
import kr.cseungjoo.chome_be.hub.domain.exception.HubPermissionDeniedException;
import kr.cseungjoo.chome_be.hub.port.in.InviteHubCommand;
import kr.cseungjoo.chome_be.hub.port.in.InviteHubResult;
import kr.cseungjoo.chome_be.hub.port.in.InviteHubUseCase;
import kr.cseungjoo.chome_be.hub.port.out.HubPermissionRepositoryPort;
import kr.cseungjoo.chome_be.hub.port.out.HubRepositoryPort;
import kr.cseungjoo.chome_be.user.application.exception.UserNotFoundException;
import kr.cseungjoo.chome_be.user.domain.User;
import kr.cseungjoo.chome_be.user.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class InviteHubService implements InviteHubUseCase {

    private final HubRepositoryPort hubRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final HubPermissionRepositoryPort hubPermissionRepositoryPort;

    @Override
    public InviteHubResult execute(InviteHubCommand command) {
        Hub hub = hubRepositoryPort.findById(command.hubId()).orElseThrow(
                () -> new HubNotFoundException("허브를 찾을 수 없습니다.")
        );

        hub.assertInvitableBy(command.userId());

        User targetUser = userRepositoryPort.findByEmail(command.targetEmail()).orElseThrow(
                () -> new UserNotFoundException("해당 이메일을 사용하는 사용자를 찾을 수 없습니다.")
        );

        List<HubPermission> permissions = new ArrayList<>();
        for (HubAction action : command.permissions()) {
            HubPermission permission = HubPermission.create(action, command.hubId(), targetUser.getId());

            permissions.add(permission);
        }

        hubPermissionRepositoryPort.save(permissions);

        InviteHubResult inviteHubResult = new InviteHubResult(
                hub.getAlias(),
                targetUser.getEmail(),
                command.permissions()
        );

        return inviteHubResult;
    }
}
