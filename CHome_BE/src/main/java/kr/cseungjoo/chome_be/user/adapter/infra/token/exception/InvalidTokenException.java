package kr.cseungjoo.chome_be.user.adapter.infra.token.exception;

import kr.cseungjoo.chome_be.user.domain.exception.UserException;

public class InvalidTokenException extends RuntimeException implements UserException {
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}