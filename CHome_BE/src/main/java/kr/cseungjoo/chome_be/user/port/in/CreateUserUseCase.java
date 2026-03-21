package kr.cseungjoo.chome_be.user.application.port.in;

import kr.cseungjoo.chome_be.user.port.in.CreateUserCommand;
import kr.cseungjoo.chome_be.user.port.in.CreateUserResult;

public interface CreateUserUseCase {
    CreateUserResult execute(CreateUserCommand command);
}
