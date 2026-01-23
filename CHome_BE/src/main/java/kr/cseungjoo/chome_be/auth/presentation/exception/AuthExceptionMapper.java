package kr.cseungjoo.chome_be.auth.presentation.exception;

import kr.cseungjoo.chome_be.auth.application.exception.AuthenticationFailedException;
import kr.cseungjoo.chome_be.auth.application.exception.EmailNotVerifiedException;
import kr.cseungjoo.chome_be.auth.application.exception.InvalidRefreshTokenException;
import kr.cseungjoo.chome_be.auth.domain.exception.AuthException;
import kr.cseungjoo.chome_be.global.exception.ApplicationException;
import kr.cseungjoo.chome_be.global.exception.ExceptionMapper;
import kr.cseungjoo.chome_be.global.response.BasicResponse;
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
        if (e instanceof ApplicationException ae) {
            return mapApplication(ae);
        }

        return defaultResponse(e);
    }

    private ResponseEntity<BasicResponse.BaseResponse> mapApplication(ApplicationException e) {
        if (e instanceof AuthenticationFailedException) {
            return BasicResponse.error(e.getMessage(), HttpStatus.UNAUTHORIZED, "A4010");
        } else if (e instanceof EmailNotVerifiedException) {
            return BasicResponse.error(e.getMessage(), HttpStatus.FORBIDDEN, "A4030");
        } else if (e instanceof InvalidRefreshTokenException) {
            return BasicResponse.error(e.getMessage(), HttpStatus.UNAUTHORIZED, "A4011");
        }

        return defaultResponse(e);
    }
}
