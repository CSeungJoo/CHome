package kr.cseungjoo.chome_be.auth.adapter.web.controller;

import jakarta.validation.Valid;
import kr.cseungjoo.chome_be.auth.adapter.web.dto.request.MqttAclRequest;
import kr.cseungjoo.chome_be.auth.adapter.web.dto.request.MqttAuthRequest;
import kr.cseungjoo.chome_be.auth.port.in.AuthenticateHubCommand;
import kr.cseungjoo.chome_be.auth.port.in.AuthenticateHubUseCase;
import kr.cseungjoo.chome_be.auth.port.in.CheckMqttAclCommand;
import kr.cseungjoo.chome_be.auth.port.in.CheckMqttAclUseCase;
import kr.cseungjoo.chome_be.shared.adapter.web.annotation.ApiV1;
import kr.cseungjoo.chome_be.shared.adapter.web.response.BasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ApiV1
@RestController
@RequestMapping("/mqtt")
@RequiredArgsConstructor
public class MqttAuthController {

    private final AuthenticateHubUseCase authenticateHubUseCase;
    private final CheckMqttAclUseCase checkMqttAclUseCase;

    @PostMapping("/auth")
    public ResponseEntity<BasicResponse.BaseResponse> authenticate(
            @Valid @RequestBody MqttAuthRequest request
    ) {
        authenticateHubUseCase.execute(
                new AuthenticateHubCommand(
                        request.clientId(),
                        request.username(),
                        request.password()
                )
        );

        return BasicResponse.ok("인증 성공");
    }

    @PostMapping("/acl")
    public ResponseEntity<BasicResponse.BaseResponse> checkAcl(
            @Valid @RequestBody MqttAclRequest request
    ) {
        checkMqttAclUseCase.execute(
                new CheckMqttAclCommand(
                        request.username(),
                        request.topic(),
                        request.acc()
                )
        );

        return BasicResponse.ok("ACL 허용");
    }
}
