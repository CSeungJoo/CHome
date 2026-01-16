package kr.cseungjoo.chome_be.user.application.exception;

import kr.cseungjoo.chome_be.global.exception.ApplicationException;
import kr.cseungjoo.chome_be.user.domain.exception.UserException;

public class AlreadyExistsUserException extends ApplicationException implements UserException {
    public AlreadyExistsUserException(String message) {
        super(message);
    }
}
