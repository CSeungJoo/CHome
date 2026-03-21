package kr.cseungjoo.chome_be.user.adapter.infra.token.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}