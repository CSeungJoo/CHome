package kr.cseungjoo.chome_be.user.domain.exception;

public class AlreadyVerifiedException extends RuntimeException implements UserException {
    public AlreadyVerifiedException(String message) {
        super(message);
    }
}