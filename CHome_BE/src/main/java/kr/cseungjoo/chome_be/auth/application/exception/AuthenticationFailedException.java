package kr.cseungjoo.chome_be.auth.application.exception;

import kr.cseungjoo.chome_be.auth.domain.exception.AuthException;
import kr.cseungjoo.chome_be.global.exception.ApplicationException;

public class AuthenticationFailedException extends ApplicationException implements AuthException {
    public AuthenticationFailedException() {
        super("이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    public AuthenticationFailedException(String message) {
        super(message);
    }
}
