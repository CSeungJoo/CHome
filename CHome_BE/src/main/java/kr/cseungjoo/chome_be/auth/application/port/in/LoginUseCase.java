package kr.cseungjoo.chome_be.auth.application.port.in;

import kr.cseungjoo.chome_be.auth.application.command.LoginCommand;
import kr.cseungjoo.chome_be.auth.application.result.LoginResult;

public interface LoginUseCase {
    LoginResult execute(LoginCommand command);
}
