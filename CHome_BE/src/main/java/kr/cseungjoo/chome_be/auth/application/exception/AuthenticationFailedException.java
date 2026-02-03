package kr.cseungjoo.chome_be.auth.application.exception;

import kr.cseungjoo.chome_be.auth.domain.exception.AuthException;

public class AuthenticationFailedException extends RuntimeException implements AuthException {
    public AuthenticationFailedException() {
        super("이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    public AuthenticationFailedException(String message) {
        super(message);
    }
}
