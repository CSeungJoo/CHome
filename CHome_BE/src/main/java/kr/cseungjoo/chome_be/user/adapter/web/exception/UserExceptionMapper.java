package kr.cseungjoo.chome_be.user.adapter.web.exception;

import kr.cseungjoo.chome_be.shared.adapter.web.exception.ExceptionMapper;
import kr.cseungjoo.chome_be.shared.adapter.web.exception.WebExceptionMetadata;
import kr.cseungjoo.chome_be.user.adapter.infra.token.exception.TokenBuildFailureException;
import kr.cseungjoo.chome_be.user.application.exception.AlreadyExistsUserException;
import kr.cseungjoo.chome_be.user.application.exception.UserNotFoundException;
import kr.cseungjoo.chome_be.user.domain.exception.*;
import kr.cseungjoo.chome_be.user.adapter.infra.token.exception.InvalidTokenException;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class UserExceptionMapper implements ExceptionMapper {
    @Override
    public boolean supports(Throwable e) {
        return e instanceof UserException
                || e instanceof InvalidTokenException
                || e instanceof TokenBuildFailureException;
    }

    @Override
    public WebExceptionMetadata map(Throwable e) {

        //domain
        if (e instanceof AlreadyVerifiedException) {
            return new WebExceptionMetadata(e.getMessage(), HttpStatus.CONFLICT, "U4090", LogLevel.WARN);
        }else if (e instanceof NameRuleViolationException) {
            return new WebExceptionMetadata(e.getMessage(), HttpStatus.BAD_REQUEST, "U4001", LogLevel.WARN);
        }

        //application
        else if (e instanceof AlreadyExistsUserException) {
            return new WebExceptionMetadata(e.getMessage(), HttpStatus.CONFLICT, "U4091", LogLevel.WARN);
        }else if(e instanceof UserNotFoundException) {
            return new WebExceptionMetadata(e.getMessage(), HttpStatus.NOT_FOUND, "U4040", LogLevel.WARN);
        }

        //infra
        else if(e instanceof InvalidTokenException) {
            return new WebExceptionMetadata(e.getMessage(), HttpStatus.BAD_REQUEST, "U4000", LogLevel.ERROR);
        }else if (e instanceof TokenBuildFailureException) {
            return new WebExceptionMetadata(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "U5000", LogLevel.ERROR);
        }

        return new WebExceptionMetadata(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "UD5000", LogLevel.ERROR);
    }
}
