package kr.cseungjoo.chome_be.common.adapter.web;

import kr.cseungjoo.chome_be.common.adapter.web.response.BasicResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final List<ExceptionMapper> mappers;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BasicResponse.BaseResponse> validationHandle(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("입력값이 올바르지 않습니다.");

        log.info("validation failed: {}", message);
        return BasicResponse.error(message, HttpStatus.BAD_REQUEST, "V4000");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BasicResponse.BaseResponse> runtimeExceptionHandle(RuntimeException e) {
        ResponseEntity<BasicResponse.BaseResponse> response = map(e);

        return response;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handle(Exception e) {
        log.error("unexpected exception", e);
        return BasicResponse.error(
                "internal server error",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "C5000"
        );
    }

    private ResponseEntity<BasicResponse.BaseResponse> map(Throwable e) {
        return mappers.stream()
                .filter(m -> m.supports(e))
                .findFirst()
                .map(m -> m.map(e))
                .orElseGet(() -> ExceptionMapper.defaultResponse(e));
    }
}
