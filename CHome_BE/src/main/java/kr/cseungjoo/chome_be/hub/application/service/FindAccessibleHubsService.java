package kr.cseungjoo.chome_be.hub.application.service;

import io.micrometer.core.annotation.Timed;
import kr.cseungjoo.chome_be.hub.port.in.FindAccessibleHubsCommand;
import kr.cseungjoo.chome_be.hub.port.in.FindAccessibleHubsUseCase;
import kr.cseungjoo.chome_be.hub.port.out.HubPermissionRepositoryPort;
import kr.cseungjoo.chome_be.hub.port.out.HubRepositoryPort;
import kr.cseungjoo.chome_be.hub.port.in.FindAccessibleHubsResult;
import kr.cseungjoo.chome_be.hub.domain.Hub;
import kr.cseungjoo.chome_be.hub.domain.HubPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindAccessibleHubsService implements FindAccessibleHubsUseCase {

    private final HubRepositoryPort hubRepositoryPort;
    private final HubPermissionRepositoryPort hubPermissionRepositoryPort;

    @Timed(value = "hub.find.accessible", description = "Time taken to find accessible hubs")
    @Override
    public FindAccessibleHubsResult execute(FindAccessibleHubsCommand command) {
        Page<Hub> hubs = hubRepositoryPort.findByUserId(command.userId(), command.pageable());

        List<Long> hubIds = hubs.getContent().stream()
                .map(Hub::getId)
                .toList();

        List<HubPermission> hubPermissions =
                hubPermissionRepositoryPort.findByUserIdAndHubIdIn(command.userId(), hubIds);

        Map<Long, List<HubPermission>> permissionByHubId =
                hubPermissions.stream()
                        .collect(Collectors.groupingBy(HubPermission::getHubId));

        List<FindAccessibleHubsResult.AccessibleHub> accessibleHubs =
                hubs.getContent().stream()
                        .filter(hub ->
                                hub.canReadBy(
                                        command.userId(),
                                        permissionByHubId.getOrDefault(hub.getId(), List.of())
                                )
                        )
                        .map(hub -> new FindAccessibleHubsResult.AccessibleHub(
                                hub.getId(),
                                hub.getSerialNumber(),
                                hub.getAlias(),
                                hub.isOwner(command.userId())
                        ))
                        .toList();

        return new FindAccessibleHubsResult(accessibleHubs, hubs.getTotalElements(), hubs.getNumber(), hubs.getSize(), hubs.hasNext());
    }

}
