package kr.cseungjoo.chome_be.hub.adapter.web.exception;

import kr.cseungjoo.chome_be.hub.application.exception.HubNotFoundException;
import kr.cseungjoo.chome_be.hub.domain.exception.HubPermissionDeniedException;
import kr.cseungjoo.chome_be.shared.adapter.web.exception.ExceptionMapper;
import kr.cseungjoo.chome_be.shared.adapter.web.exception.WebExceptionMetadata;
import kr.cseungjoo.chome_be.hub.application.exception.AlreadyExistsHubException;
import kr.cseungjoo.chome_be.hub.domain.exception.HubException;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class HubExceptionMapper implements ExceptionMapper {
    @Override
    public boolean supports(Throwable e) {
        return e instanceof HubException;
    }

    @Override
    public WebExceptionMetadata map(Throwable e) {

        //application
        if (e instanceof AlreadyExistsHubException) {
            return new WebExceptionMetadata(e.getMessage(), HttpStatus.CONFLICT, "H4090", LogLevel.WARN);
        } else if (e instanceof HubNotFoundException) {
            return new WebExceptionMetadata(e.getMessage(), HttpStatus.NOT_FOUND, "H4040", LogLevel.WARN);
        } else if (e instanceof HubPermissionDeniedException) {
            return new WebExceptionMetadata(e.getMessage(), HttpStatus.FORBIDDEN, "H4030", LogLevel.WARN);
        }

        return new WebExceptionMetadata(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "HD5000", LogLevel.ERROR);

    }
}
