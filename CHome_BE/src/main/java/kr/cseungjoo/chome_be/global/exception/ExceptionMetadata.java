package kr.cseungjoo.chome_be.global.exception;

import org.springframework.http.HttpStatus;

public record ExceptionMetadata(
        String message,
        HttpStatus status,
        String code,
        LogLevel logLevel
) {
        public enum LogLevel { INFO, WARN, ERROR }
}
