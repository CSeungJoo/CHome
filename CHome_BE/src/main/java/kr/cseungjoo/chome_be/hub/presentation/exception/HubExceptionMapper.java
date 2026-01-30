package kr.cseungjoo.chome_be.hub.presentation.exception;

import kr.cseungjoo.chome_be.global.exception.ApplicationException;
import kr.cseungjoo.chome_be.global.exception.DomainException;
import kr.cseungjoo.chome_be.global.exception.ExceptionMapper;
import kr.cseungjoo.chome_be.global.exception.InfraException;
import kr.cseungjoo.chome_be.global.response.BasicResponse;
import kr.cseungjoo.chome_be.hub.application.exception.AlreadyExistsHubException;
import kr.cseungjoo.chome_be.hub.domain.exception.HubException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class HubExceptionMapper implements ExceptionMapper {
    @Override
    public boolean supports(Throwable e) {
        return e instanceof HubException;
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

        return BasicResponse.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "HD5000");

    }

    private ResponseEntity<BasicResponse.BaseResponse> mapDomain(DomainException e) {

        return defaultResponse(e);
    }

    private ResponseEntity<BasicResponse.BaseResponse> mapApplication(ApplicationException e) {

        if (e instanceof AlreadyExistsHubException) {
            return BasicResponse.error(e.getMessage(), HttpStatus.CONFLICT, "H4090");
        }

        return defaultResponse(e);
    }

    private ResponseEntity<BasicResponse.BaseResponse> mapInfra(InfraException e) {

        return defaultResponse(e);
    }
}
