package kr.cseungjoo.chome_be.user.adapter.infra.token.exception;

import kr.cseungjoo.chome_be.user.domain.exception.UserException;

public class TokenBuildFailureException extends RuntimeException implements UserException {
    public TokenBuildFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
