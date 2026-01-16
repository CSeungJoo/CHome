package kr.cseungjoo.chome_be.user.presentation.exception;

import kr.cseungjoo.chome_be.global.exception.ApplicationException;
import kr.cseungjoo.chome_be.global.exception.DomainException;
import kr.cseungjoo.chome_be.global.exception.ExceptionMapper;
import kr.cseungjoo.chome_be.global.exception.InfraException;
import kr.cseungjoo.chome_be.global.response.BasicResponse;
import kr.cseungjoo.chome_be.user.application.exception.AlreadyExistsUserException;
import kr.cseungjoo.chome_be.user.application.exception.UserNotFoundException;
import kr.cseungjoo.chome_be.user.domain.exception.*;
import kr.cseungjoo.chome_be.user.infra.token.exception.InvalidTokenException;
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

        if (e instanceof DomainException de) {
            return mapDomain(de);
        }

        if (e instanceof ApplicationException ae) {
            return mapApplication(ae);
        }

        if (e instanceof InfraException ie) {
            return mapInfra(ie);
        }

        return BasicResponse.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "UD5000");
    }

    private ResponseEntity<BasicResponse.BaseResponse> mapDomain(DomainException e) {

        if (e instanceof AlreadyVerifiedException) {
            return BasicResponse.error(e.getMessage(), HttpStatus.CONFLICT, "U4090");
        }else if (e instanceof NameRuleViolationException) {
            return BasicResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST, "U4001");
        }

        return defaultResponse(e);
    }

    private ResponseEntity<BasicResponse.BaseResponse> mapApplication(ApplicationException e) {
        if (e instanceof AlreadyExistsUserException) {
            return BasicResponse.error(e.getMessage(), HttpStatus.CONFLICT, "U4091");
        }else if(e instanceof UserNotFoundException) {
            return BasicResponse.error(e.getMessage(), HttpStatus.NOT_FOUND, "U4040");
        }

        return defaultResponse(e);
    }

    private ResponseEntity<BasicResponse.BaseResponse> mapInfra(InfraException e) {
        if(e instanceof InvalidTokenException) {
            return BasicResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST, "U4000");
        }

        return defaultResponse(e);
    }
}
