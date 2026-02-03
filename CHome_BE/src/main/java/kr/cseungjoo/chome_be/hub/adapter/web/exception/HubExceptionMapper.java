package kr.cseungjoo.chome_be.hub.adapter.web.exception;

import kr.cseungjoo.chome_be.common.adapter.web.ExceptionMapper;
import kr.cseungjoo.chome_be.common.adapter.web.response.BasicResponse;
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

        if (e instanceof AlreadyExistsHubException) {
            return BasicResponse.error(e.getMessage(), HttpStatus.CONFLICT, "H4090");
        }

        return BasicResponse.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "HD5000");

    }
}
