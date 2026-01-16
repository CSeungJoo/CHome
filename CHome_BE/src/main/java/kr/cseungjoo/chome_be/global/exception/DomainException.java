package kr.cseungjoo.chome_be.global.exception;

public class DomainException extends BasicException {
    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
