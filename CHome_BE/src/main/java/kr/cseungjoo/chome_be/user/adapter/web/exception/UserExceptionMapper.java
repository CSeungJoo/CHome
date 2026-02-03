package kr.cseungjoo.chome_be.user.adapter.web.exception;

import kr.cseungjoo.chome_be.common.adapter.web.ExceptionMapper;
import kr.cseungjoo.chome_be.common.adapter.web.response.BasicResponse;
import kr.cseungjoo.chome_be.user.adapter.infra.token.exception.TokenBuildFailureException;
import kr.cseungjoo.chome_be.user.application.exception.AlreadyExistsUserException;
import kr.cseungjoo.chome_be.user.application.exception.UserNotFoundException;
import kr.cseungjoo.chome_be.user.domain.exception.*;
import kr.cseungjoo.chome_be.user.adapter.infra.token.exception.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserExceptionMapper implements ExceptionMapper {
    @Override
    public boolean supports(Throwable e) {
        return e instanceof UserException;
    }

    @Override
    public ResponseEntity<BasicResponse.BaseResponse> map(Throwable e) {

        //domain
        if (e instanceof AlreadyVerifiedException) {
            return BasicResponse.error(e.getMessage(), HttpStatus.CONFLICT, "U4090");
        }else if (e instanceof NameRuleViolationException) {
            return BasicResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST, "U4001");
        }

        //application
        else if (e instanceof AlreadyExistsUserException) {
            return BasicResponse.error(e.getMessage(), HttpStatus.CONFLICT, "U4091");
        }else if(e instanceof UserNotFoundException) {
            return BasicResponse.error(e.getMessage(), HttpStatus.NOT_FOUND, "U4040");
        }

        //infra
        else if(e instanceof InvalidTokenException) {
            return BasicResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST, "U4000");
        }else if (e instanceof TokenBuildFailureException) {
            return BasicResponse.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "U5000");
        }

        return BasicResponse.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "UD5000");
    }
}
