package kr.cseungjoo.chome_be.user.infra.token.exception;

import kr.cseungjoo.chome_be.global.exception.InfraException;
import kr.cseungjoo.chome_be.user.domain.exception.UserException;

public class TokenBuildFailureException extends InfraException implements UserException {
    public TokenBuildFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
