package kr.cseungjoo.chome_be.user.adapter.web.controller;

import kr.cseungjoo.chome_be.shared.adapter.web.response.BasicResponse;
import kr.cseungjoo.chome_be.user.adapter.web.dto.request.CreateUserRequest;
import kr.cseungjoo.chome_be.user.adapter.web.dto.response.CreateUserResponse;
import kr.cseungjoo.chome_be.user.application.command.CreateUserCommand;
import kr.cseungjoo.chome_be.user.application.port.in.CreateUserUseCase;
import kr.cseungjoo.chome_be.user.application.port.in.VerifyEmailUseCase;
import kr.cseungjoo.chome_be.user.application.result.CreateUserResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final VerifyEmailUseCase verifyEmailUseCase;

    @PostMapping
    public ResponseEntity<BasicResponse.BaseResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        CreateUserResult result = createUserUseCase.execute(
                new CreateUserCommand(
                        createUserRequest.name(),
                        createUserRequest.email(),
                        createUserRequest.password()
                )
        );

        CreateUserResponse createUserResponse = new CreateUserResponse(
                result.name(),
                result.email(),
                result.EmailVerified(),
                result.createdAt(),
                result.lastLogin()
        );

        return BasicResponse.created(createUserResponse);
    }

    @GetMapping("/verify")
    public ResponseEntity<BasicResponse.BaseResponse> verifyEmail(@RequestParam String token) {
        verifyEmailUseCase.execute(token);

        return BasicResponse.ok("이메일 인증이 완료되었습니다.");
    }
}
