package kr.cseungjoo.chome_be.user.domain.exception;


import kr.cseungjoo.chome_be.global.exception.DomainException;

public class AlreadyVerifiedException extends DomainException implements UserException {
    public AlreadyVerifiedException(String message) {
        super(message);
    }
}