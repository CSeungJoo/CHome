package kr.cseungjoo.chome_be.shared.adapter.web.exception;

import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

public interface ExceptionMapper {
    boolean supports(Throwable e);
    WebExceptionMetadata map(Throwable e);
    static WebExceptionMetadata defaultMetadata(Throwable e) {
        return new WebExceptionMetadata("알 수 없는 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR, "SD5000", LogLevel.ERROR);
    }
}