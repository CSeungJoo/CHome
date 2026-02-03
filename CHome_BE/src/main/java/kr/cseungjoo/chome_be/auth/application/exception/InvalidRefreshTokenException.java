package kr.cseungjoo.chome_be.auth.application.exception;

import kr.cseungjoo.chome_be.auth.domain.exception.AuthException;

public class InvalidRefreshTokenException extends RuntimeException implements AuthException {
    public InvalidRefreshTokenException() {
        super("유효하지 않은 리프레시 토큰입니다.");
    }

    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
