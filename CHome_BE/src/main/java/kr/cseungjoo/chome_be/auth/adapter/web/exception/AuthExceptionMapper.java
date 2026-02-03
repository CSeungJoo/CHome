package kr.cseungjoo.chome_be.auth.adapter.web.exception;

import kr.cseungjoo.chome_be.auth.application.exception.AuthenticationFailedException;
import kr.cseungjoo.chome_be.auth.application.exception.EmailNotVerifiedException;
import kr.cseungjoo.chome_be.auth.application.exception.InvalidRefreshTokenException;
import kr.cseungjoo.chome_be.auth.domain.exception.AuthException;
import kr.cseungjoo.chome_be.common.adapter.web.ExceptionMapper;
import kr.cseungjoo.chome_be.common.adapter.web.response.BasicResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthExceptionMapper implements ExceptionMapper {

    @Override
    public boolean supports(Throwable e) {
        return e instanceof AuthException;
    }

    @Override
    public ResponseEntity<BasicResponse.BaseResponse> map(Throwable e) {

        // application
        if (e instanceof AuthenticationFailedException) {
            return BasicResponse.error(e.getMessage(), HttpStatus.UNAUTHORIZED, "A4010");
        } else if (e instanceof EmailNotVerifiedException) {
            return BasicResponse.error(e.getMessage(), HttpStatus.FORBIDDEN, "A4030");
        } else if (e instanceof InvalidRefreshTokenException) {
            return BasicResponse.error(e.getMessage(), HttpStatus.UNAUTHORIZED, "A4011");
        }

        return ExceptionMapper.defaultResponse(e);
    }
}
