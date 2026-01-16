package kr.cseungjoo.chome_be.global.exception;

import kr.cseungjoo.chome_be.global.response.BasicResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final List<ExceptionMapper> mappers;

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<BasicResponse.BaseResponse> domainHandle(DomainException e) {
        log.warn("domain rule violated: {}", e.getMessage());
        return map(e);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<BasicResponse.BaseResponse> applicationHandle(ApplicationException e) {
        log.info("use case failed: {}", e.getMessage());
        return map(e);
    }

    @ExceptionHandler(InfraException.class)
    public ResponseEntity<BasicResponse.BaseResponse> infraHandle(InfraException e) {
        log.error("infra error", e);
        return map(e);
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
                .orElseThrow();
    }
}
