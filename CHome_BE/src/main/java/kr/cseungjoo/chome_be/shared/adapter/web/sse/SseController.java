package kr.cseungjoo.chome_be.shared.adapter.web.sse;

import kr.cseungjoo.chome_be.hub.application.exception.HubNotFoundException;
import kr.cseungjoo.chome_be.hub.domain.Hub;
import kr.cseungjoo.chome_be.hub.domain.HubPermission;
import kr.cseungjoo.chome_be.hub.port.out.HubPermissionRepositoryPort;
import kr.cseungjoo.chome_be.hub.port.out.HubRepositoryPort;
import kr.cseungjoo.chome_be.shared.adapter.web.annotation.ApiV1;
import kr.cseungjoo.chome_be.shared.adapter.web.context.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@ApiV1
@RestController
@RequestMapping("/hubs")
@RequiredArgsConstructor
public class SseController {

    private final SseEmitterRegistry sseEmitterRegistry;
    private final HubRepositoryPort hubRepositoryPort;
    private final HubPermissionRepositoryPort hubPermissionRepositoryPort;

    @GetMapping(value = "/{hubId}/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long hubId
    ) {
        Hub hub = hubRepositoryPort.findById(hubId)
                .orElseThrow(() -> new HubNotFoundException("허브를 찾을 수 없습니다."));

        List<HubPermission> permissions = hubPermissionRepositoryPort
                .findByUserIdAndHubId(user.userId(), hubId);

        if (!hub.canReadBy(user.userId(), permissions)) {
            throw new HubNotFoundException("허브를 찾을 수 없습니다.");
        }

        return sseEmitterRegistry.connect(hubId, user.userId());
    }
}
