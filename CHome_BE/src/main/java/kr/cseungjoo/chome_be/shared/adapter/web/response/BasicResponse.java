package kr.cseungjoo.chome_be.shared.adapter.web.response;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record BasicResponse<T>(T data, int status) {

    public static ResponseEntity<BaseResponse> error(String msg, String code, HttpStatus status) {
        BaseErrorResponse errorResponse = new BaseErrorResponse(msg, code);
        BaseResponse response = new BaseResponse(BaseStatus.ERROR, errorResponse);
        return ResponseEntity
                .status(status)
                .body(response);
    }

    public static ResponseEntity<BaseResponse> error(String msg, HttpStatus status, String code) {
        BaseErrorResponse errorResponse = new BaseErrorResponse(msg, code);
        BaseResponse response = new BaseResponse(BaseStatus.ERROR, errorResponse);
        return ResponseEntity
                .status(status)
                .body(response);
    }

    public static ResponseEntity<BaseResponse> ok(Object data, HttpHeaders headers) {
        BaseResponse response = new BaseResponse(BaseStatus.SUCCESS, data);
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(response);
    }

    public static ResponseEntity<BaseResponse> ok(Object data) {
        BaseResponse response = new BaseResponse(BaseStatus.SUCCESS, data);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    public static ResponseEntity<BaseResponse> ok(String msg) {
        MsgResponse msgResponse = new MsgResponse(msg);
        BaseResponse response = new BaseResponse(BaseStatus.SUCCESS, msgResponse);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    public static ResponseEntity<BaseResponse> customStatus(Object data, HttpStatus httpStatus, BaseStatus baseStatus) {
        BaseResponse response = new BaseResponse(baseStatus, data);
        return ResponseEntity
                .status(httpStatus)
                .body(response);
    }

    public static ResponseEntity<BaseResponse> customStatus(String data, HttpStatus httpStatus, BaseStatus baseStatus) {
        MsgResponse msgResponse = new MsgResponse(data);
        BaseResponse response = new BaseResponse(baseStatus, msgResponse);
        return ResponseEntity
                .status(httpStatus)
                .body(response);
    }

    public static ResponseEntity<BaseResponse> created(Object data) {
        BaseResponse response = new BaseResponse(BaseStatus.SUCCESS, data);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    public record BaseResponse(BaseStatus status, Object data) {
    }

    public record BaseErrorResponse(String message, String code) {
    }

    public enum BaseStatus {
        SUCCESS, ERROR
    }

    public record MsgResponse(String message) {
    }
}