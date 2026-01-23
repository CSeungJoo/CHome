package kr.cseungjoo.chome_be.auth.application.exception;

import kr.cseungjoo.chome_be.auth.domain.exception.AuthException;
import kr.cseungjoo.chome_be.global.exception.ApplicationException;

public class InvalidRefreshTokenException extends ApplicationException implements AuthException {
    public InvalidRefreshTokenException() {
        super("유효하지 않은 리프레시 토큰입니다.");
    }

    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
