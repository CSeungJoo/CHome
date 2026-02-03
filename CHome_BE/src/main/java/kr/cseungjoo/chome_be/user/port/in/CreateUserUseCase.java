package kr.cseungjoo.chome_be.user.application.port.in;

import kr.cseungjoo.chome_be.user.application.command.CreateUserCommand;
import kr.cseungjoo.chome_be.user.application.result.CreateUserResult;

public interface CreateUserUseCase {
    CreateUserResult execute(CreateUserCommand command);
}
