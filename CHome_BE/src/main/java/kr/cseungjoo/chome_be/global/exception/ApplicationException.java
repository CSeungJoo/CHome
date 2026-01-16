package kr.cseungjoo.chome_be.global.exception;

public class ApplicationException extends BasicException {
    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
