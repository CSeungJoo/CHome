package kr.cseungjoo.chome_be.global.exception;

import kr.cseungjoo.chome_be.global.response.BasicResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface ExceptionMapper {
    boolean supports(Throwable e);
    ResponseEntity<BasicResponse.BaseResponse> map(Throwable e);
    default ResponseEntity<BasicResponse.BaseResponse> defaultResponse(Throwable e) {
        return BasicResponse.error("알 수 없는 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR, "SD5000");
    }
}