package kr.cseungjoo.chome_be.hub.domain.exception;

public class HubPermissionDeniedException extends RuntimeException implements HubException{
    public HubPermissionDeniedException(String message) {
        super(message);
    }
}
