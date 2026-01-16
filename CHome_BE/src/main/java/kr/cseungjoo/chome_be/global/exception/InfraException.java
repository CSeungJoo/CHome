package kr.cseungjoo.chome_be.global.exception;

public class InfraException extends BasicException {
    public InfraException(String message) {
        super(message);
    }

    public InfraException(String message, Throwable cause) {
        super(message, cause);
    }
}
