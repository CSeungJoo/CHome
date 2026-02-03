package kr.cseungjoo.chome_be.auth.port.in;

import kr.cseungjoo.chome_be.auth.application.command.RefreshCommand;
import kr.cseungjoo.chome_be.auth.application.result.RefreshResult;

public interface RefreshUseCase {
    RefreshResult execute(RefreshCommand command);
}
