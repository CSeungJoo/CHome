package kr.cseungjoo.chome_be.auth.adapter.web.controller;

import kr.cseungjoo.chome_be.auth.adapter.web.dto.request.LoginRequest;
import kr.cseungjoo.chome_be.auth.adapter.web.dto.request.RefreshRequest;
import kr.cseungjoo.chome_be.auth.adapter.web.dto.response.LoginResponse;
import kr.cseungjoo.chome_be.auth.application.command.LoginCommand;
import kr.cseungjoo.chome_be.auth.application.command.RefreshCommand;
import kr.cseungjoo.chome_be.auth.port.in.LoginUseCase;
import kr.cseungjoo.chome_be.auth.port.in.RefreshUseCase;
import kr.cseungjoo.chome_be.auth.application.result.LoginResult;
import kr.cseungjoo.chome_be.auth.application.result.RefreshResult;
import kr.cseungjoo.chome_be.auth.adapter.web.dto.response.RefreshResponse;
import kr.cseungjoo.chome_be.common.adapter.web.response.BasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RefreshUseCase refreshUseCase;

    @PostMapping("/login")
    public ResponseEntity<BasicResponse.BaseResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResult result = loginUseCase.execute(
                new LoginCommand(request.email(), request.password())
        );

        LoginResponse response = new LoginResponse(
                result.accessToken(),
                result.refreshToken(),
                result.userId()
        );

        return BasicResponse.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<BasicResponse.BaseResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        RefreshResult result = refreshUseCase.execute(
                new RefreshCommand(request.refreshToken())
        );

        RefreshResponse response = new RefreshResponse(
                result.accessToken(),
                result.refreshToken()
        );

        return BasicResponse.ok(response);
    }
}
