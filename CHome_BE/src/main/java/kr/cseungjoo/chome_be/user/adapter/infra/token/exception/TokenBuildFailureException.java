package kr.cseungjoo.chome_be.user.adapter.infra.token.exception;

public class TokenBuildFailureException extends RuntimeException {
    public TokenBuildFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
