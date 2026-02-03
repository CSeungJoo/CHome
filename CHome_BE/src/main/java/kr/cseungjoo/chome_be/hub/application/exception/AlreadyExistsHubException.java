package kr.cseungjoo.chome_be.hub.application.exception;

import kr.cseungjoo.chome_be.hub.domain.exception.HubException;

public class AlreadyExistsHubException extends RuntimeException implements HubException {
    public AlreadyExistsHubException(String message) {
        super(message);
    }
}