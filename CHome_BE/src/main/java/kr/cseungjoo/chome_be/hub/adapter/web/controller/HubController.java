package kr.cseungjoo.chome_be.hub.adapter.web.controller;

import jakarta.validation.Valid;
import kr.cseungjoo.chome_be.common.adapter.web.context.AuthenticatedUser;
import kr.cseungjoo.chome_be.common.adapter.web.response.BasicResponse;
import kr.cseungjoo.chome_be.hub.application.command.FindAccessibleHubsCommand;
import kr.cseungjoo.chome_be.hub.application.command.RegisterHubCommand;
import kr.cseungjoo.chome_be.hub.port.in.FindAccessibleHubsUseCase;
import kr.cseungjoo.chome_be.hub.port.in.RegisterHubUseCase;
import kr.cseungjoo.chome_be.hub.application.result.FindAccessibleHubsResult;
import kr.cseungjoo.chome_be.hub.application.result.RegisterHubResult;
import kr.cseungjoo.chome_be.hub.adapter.web.dto.request.RegisterHubRequest;
import kr.cseungjoo.chome_be.hub.adapter.web.dto.response.GetAccessibleHubsResponse;
import kr.cseungjoo.chome_be.hub.adapter.web.dto.response.RegisterHubResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hubs")
@RequiredArgsConstructor
public class HubController {

    private final RegisterHubUseCase registerHubUseCase;
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
}
