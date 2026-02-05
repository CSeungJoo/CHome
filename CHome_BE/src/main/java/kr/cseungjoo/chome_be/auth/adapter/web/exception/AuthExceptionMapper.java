package kr.cseungjoo.chome_be.auth.adapter.web.exception;

import kr.cseungjoo.chome_be.auth.application.exception.AuthenticationFailedException;
import kr.cseungjoo.chome_be.auth.application.exception.EmailNotVerifiedException;
import kr.cseungjoo.chome_be.auth.application.exception.InvalidRefreshTokenException;
import kr.cseungjoo.chome_be.auth.domain.exception.AuthException;
import kr.cseungjoo.chome_be.shared.adapter.web.exception.ExceptionMapper;
import kr.cseungjoo.chome_be.shared.adapter.web.exception.WebExceptionMetadata;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class AuthExceptionMapper implements ExceptionMapper {

    @Override
    public boolean supports(Throwable e) {
        return e instanceof AuthException;
    }

    @Override
    public WebExceptionMetadata map(Throwable e) {

        // application
        if (e instanceof AuthenticationFailedException) {
            return new WebExceptionMetadata(e.getMessage(), HttpStatus.UNAUTHORIZED, "A4010", LogLevel.WARN);
        } else if (e instanceof EmailNotVerifiedException) {
            return new WebExceptionMetadata(e.getMessage(), HttpStatus.FORBIDDEN, "A4030", LogLevel.WARN);
        } else if (e instanceof InvalidRefreshTokenException) {
            return new WebExceptionMetadata(e.getMessage(), HttpStatus.UNAUTHORIZED, "A4011", LogLevel.WARN);
        }

        return new WebExceptionMetadata(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "AD5000", LogLevel.ERROR);
    }
}
