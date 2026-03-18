package kr.cseungjoo.chome_be.device.adapter.web.exception;

import kr.cseungjoo.chome_be.device.application.exception.DeviceNotFoundException;
import kr.cseungjoo.chome_be.device.domain.exception.DeviceException;
import kr.cseungjoo.chome_be.device.domain.exception.DevicePermissionDeniedException;
import kr.cseungjoo.chome_be.shared.adapter.web.exception.ExceptionMapper;
import kr.cseungjoo.chome_be.shared.adapter.web.exception.WebExceptionMetadata;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class DeviceExceptionMapper implements ExceptionMapper {
    @Override
    public boolean supports(Throwable e) {
        return e instanceof DeviceException;
    }

    @Override
    public WebExceptionMetadata map(Throwable e) {

        if (e instanceof DeviceNotFoundException) {
            return new WebExceptionMetadata(e.getMessage(), HttpStatus.NOT_FOUND, "DD4040", LogLevel.WARN);
        }

        if (e instanceof DevicePermissionDeniedException) {
            return new WebExceptionMetadata(e.getMessage(), HttpStatus.FORBIDDEN, "DD4030", LogLevel.WARN);
        }

        return new WebExceptionMetadata(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "DD5000", LogLevel.ERROR);
    }
}
