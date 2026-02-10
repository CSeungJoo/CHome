package kr.cseungjoo.chome_be.hub.application.exception;

import kr.cseungjoo.chome_be.hub.domain.exception.HubException;

public class HubNotFoundException extends RuntimeException implements HubException {
    public HubNotFoundException(String message) {
        super(message);
    }
}
