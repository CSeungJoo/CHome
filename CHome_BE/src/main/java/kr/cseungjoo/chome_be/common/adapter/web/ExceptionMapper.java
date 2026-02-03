package kr.cseungjoo.chome_be.common.adapter.web;

import kr.cseungjoo.chome_be.common.adapter.web.response.BasicResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface ExceptionMapper {
    boolean supports(Throwable e);
    ResponseEntity<BasicResponse.BaseResponse> map(Throwable e);
    static ResponseEntity<BasicResponse.BaseResponse> defaultResponse(Throwable e) {
        return BasicResponse.error("알 수 없는 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR, "SD5000");
    }
}