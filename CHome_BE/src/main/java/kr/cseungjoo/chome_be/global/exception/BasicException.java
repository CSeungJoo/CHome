package kr.cseungjoo.chome_be.global.exception;

public class BasicException extends RuntimeException{
    public BasicException(String message) {
        super(message);
    }

    public BasicException(String message, Throwable cause) {
        super(message, cause);
    }
}
