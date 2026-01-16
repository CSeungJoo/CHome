package kr.cseungjoo.chome_be.user.infra.token.exception;

import kr.cseungjoo.chome_be.global.exception.InfraException;
import kr.cseungjoo.chome_be.user.domain.exception.UserException;

public class InvalidTokenException extends InfraException implements UserException {
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}