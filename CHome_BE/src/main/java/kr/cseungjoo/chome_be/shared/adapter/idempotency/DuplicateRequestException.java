package kr.cseungjoo.chome_be.shared.adapter.idempotency;

public class DuplicateRequestException extends RuntimeException {
    public DuplicateRequestException(String message) {
        super(message);
    }
}
