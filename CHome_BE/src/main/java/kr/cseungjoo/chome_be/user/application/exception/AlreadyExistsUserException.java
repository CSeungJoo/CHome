package kr.cseungjoo.chome_be.user.application.exception;

import kr.cseungjoo.chome_be.user.domain.exception.UserException;

public class AlreadyExistsUserException extends RuntimeException implements UserException {
    public AlreadyExistsUserException(String message) {
        super(message);
    }
}
