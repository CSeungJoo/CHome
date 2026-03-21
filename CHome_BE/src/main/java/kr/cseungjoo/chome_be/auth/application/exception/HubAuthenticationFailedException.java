package kr.cseungjoo.chome_be.auth.application.exception;

import kr.cseungjoo.chome_be.auth.domain.exception.AuthException;

public class HubAuthenticationFailedException extends RuntimeException implements AuthException {
    public HubAuthenticationFailedException() {
        super("등록되지 않은 허브입니다.");
    }
}
