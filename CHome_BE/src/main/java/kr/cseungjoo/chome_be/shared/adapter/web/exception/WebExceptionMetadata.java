package kr.cseungjoo.chome_be.shared.adapter.web.exception;

import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

public record WebExceptionMetadata(
        String message,
        HttpStatus status,
        String code,
        LogLevel logLevel
) {
}
