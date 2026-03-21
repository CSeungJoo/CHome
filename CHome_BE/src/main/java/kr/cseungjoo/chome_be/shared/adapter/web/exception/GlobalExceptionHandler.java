package kr.cseungjoo.chome_be.shared.adapter.web.exception;

import kr.cseungjoo.chome_be.shared.adapter.web.response.BasicResponse;
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

        log.warn("validation failed: {}", message);
        return BasicResponse.error(message, HttpStatus.BAD_REQUEST, "V4000");
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<BasicResponse.BaseResponse> runtimeExceptionHandle(Throwable e) {
        WebExceptionMetadata metadata = map(e);

        switch (metadata.logLevel()) {
            case WARN  -> log.warn(metadata.message(), e);
            case ERROR -> log.error(metadata.message(), e);
            default    -> log.error("unchecked exception: {}", metadata.message(), e);
        }

        return BasicResponse.error(metadata.message(), metadata.status(), metadata.code());
    }

    private WebExceptionMetadata map(Throwable e) {
        return mappers.stream()
                .filter(m -> m.supports(e))
                .findFirst()
                .map(m -> m.map(e))
                .orElseGet(() -> ExceptionMapper.defaultMetadata(e));
    }
}
