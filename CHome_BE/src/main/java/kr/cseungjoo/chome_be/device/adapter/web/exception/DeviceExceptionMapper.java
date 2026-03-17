package kr.cseungjoo.chome_be.device.adapter.web.exception;

import kr.cseungjoo.chome_be.device.domain.exception.DeviceException;
import kr.cseungjoo.chome_be.shared.adapter.web.exception.ExceptionMapper;
import kr.cseungjoo.chome_be.shared.adapter.web.exception.WebExceptionMetadata;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

public class DeviceExceptionMapper implements ExceptionMapper {
    @Override
    public boolean supports(Throwable e) {
        return e instanceof DeviceException;
    }

    @Override
    public WebExceptionMetadata map(Throwable e) {

        return new WebExceptionMetadata(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "DD5000", LogLevel.ERROR);
    }
}
