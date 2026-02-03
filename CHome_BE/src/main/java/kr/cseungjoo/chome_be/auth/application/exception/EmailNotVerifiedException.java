package kr.cseungjoo.chome_be.auth.application.exception;

import kr.cseungjoo.chome_be.auth.domain.exception.AuthException;

public class EmailNotVerifiedException extends RuntimeException implements AuthException {
    public EmailNotVerifiedException() {
        super("이메일 인증이 완료되지 않았습니다.");
    }

    public EmailNotVerifiedException(String message) {
        super(message);
    }
}
