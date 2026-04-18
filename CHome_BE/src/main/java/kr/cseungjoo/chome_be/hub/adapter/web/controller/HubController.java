package kr.cseungjoo.chome_be.hub.adapter.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.cseungjoo.chome_be.hub.adapter.web.dto.request.ChangeHubAliasRequest;
import kr.cseungjoo.chome_be.hub.adapter.web.dto.request.InviteHubRequest;
import kr.cseungjoo.chome_be.hub.adapter.web.dto.request.RegisterHubRequest;
import kr.cseungjoo.chome_be.hub.adapter.web.dto.request.SendHubCommandRequest;
import kr.cseungjoo.chome_be.hub.adapter.web.dto.response.*;
import kr.cseungjoo.chome_be.hub.port.in.*;
import kr.cseungjoo.chome_be.shared.adapter.web.annotation.ApiV1;
import kr.cseungjoo.chome_be.shared.adapter.web.context.AuthenticatedUser;
import kr.cseungjoo.chome_be.shared.adapter.web.response.BasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@ApiV1
@RestController
@RequestMapping("/hubs")
@RequiredArgsConstructor
public class HubController {

    private final InviteHubUseCase inviteHubUseCase;
    private final DeleteHubUseCase deleteHubUseCase;
    private final RegisterHubUseCase registerHubUseCase;
    private final ChangeHubAliasUseCase changeHubAliasUseCase;
    private final SendHubCommandUseCase sendHubCommandUseCase;
    private final FindAccessibleHubsUseCase findAccessibleHubsUseCase;

    @PostMapping
    public ResponseEntity<BasicResponse.BaseResponse> registerHub(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody RegisterHubRequest registerHubRequest
    ) {
        RegisterHubResult result = registerHubUseCase.execute(
                new RegisterHubCommand(
                        registerHubRequest.serialNumber(),
                        registerHubRequest.alias(),
                        authenticatedUser.userId()
                )
        );

        RegisterHubResponse registerHubResponse = new RegisterHubResponse(
                result.serialNumber(),
                result.alias(),
                result.createdAt()
        );

        return BasicResponse.ok(registerHubResponse);
    }

    @GetMapping
    public ResponseEntity<BasicResponse.BaseResponse> getAccessibleHubs(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PageableDefault Pageable pageable
            ) {
        FindAccessibleHubsResult result = findAccessibleHubsUseCase.execute(
                new FindAccessibleHubsCommand(
                        authenticatedUser.userId(),
                        pageable
                )
        );

        GetAccessibleHubsResponse response = new GetAccessibleHubsResponse(
                result.hubs().stream()
                        .map(h ->
                                new GetAccessibleHubsResponse.AccessibleHub(
                                        h.id(),
                                        h.serialNumber(),
                                        h.alias(),
                                        h.isOwner()
                                )
                        ).toList(),
                result.totalCount(),
                result.page(),
                result.size(),
                result.hasNext()
        );

        return BasicResponse.ok(response);
    }

    @DeleteMapping("/{hubId}")
    public ResponseEntity<BasicResponse.BaseResponse> deleteHub(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long hubId
    ) {

        DeleteHubResult result = deleteHubUseCase.execute(
                new DeleteHubCommand(
                        authenticatedUser.userId(),
                        hubId
                )
        );

        DeleteHubResponse deleteHubResponse = new DeleteHubResponse(
                result.serialNumber(),
                result.deletedAt()
        );

        return BasicResponse.ok(deleteHubResponse);
    }

    @PutMapping("/{hubId}")
    public ResponseEntity<BasicResponse.BaseResponse> changeHubAlias(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long hubId,
            @Valid @RequestBody @NotNull ChangeHubAliasRequest request
            ) {

        ChangeHubAliasResult result = changeHubAliasUseCase.execute(
                new ChangeHubAliasCommand(
                        hubId,
                        request.alias(),
                        authenticatedUser.userId()
                )
        );

        ChangeHubAliasResponse changeHubAliasResponse = new ChangeHubAliasResponse(
                result.alias(),
                result.changedAt()
        );

        return BasicResponse.ok(changeHubAliasResponse);
    }

    @PostMapping("/{hubId}/command")
    public ResponseEntity<BasicResponse.BaseResponse> sendCommand(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long hubId,
            @Valid @RequestBody SendHubCommandRequest request
    ) {
        SendHubCommandResult result = sendHubCommandUseCase.execute(
                new SendHubCommandCommand(
                        authenticatedUser.userId(),
                        hubId,
                        request.type(),
                        request.payload() != null ? request.payload() : java.util.Map.of()
                )
        );

        SendHubCommandResponse response = new SendHubCommandResponse(
                result.requestId(),
                result.type()
        );

        return BasicResponse.ok(response);
    }

    @PostMapping("/{hubId}/invite")
    public ResponseEntity<BasicResponse.BaseResponse> inviteHub(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long hubId,
            @Valid @RequestBody InviteHubRequest request
    ) {
        InviteHubResult result = inviteHubUseCase.execute(
                new InviteHubCommand(
                        authenticatedUser.userId(),
                        hubId,
                        request.targetEmail(),
                        request.permissions()
                )
        );

        InviteHubResponse response = new InviteHubResponse(
                result.hubAlias(),
                result.targetEmail(),
                result.permissions()
        );

        return BasicResponse.ok(response);
    }
}
