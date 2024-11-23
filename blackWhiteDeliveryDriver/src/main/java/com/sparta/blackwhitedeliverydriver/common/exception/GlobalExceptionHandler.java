package com.sparta.blackwhitedeliverydriver.common.exception;

import com.sparta.blackwhitedeliverydriver.user.exception.ExceptionMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<RestApiException> illegalArgumentExceptionHandler(IllegalArgumentException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
                // HTTP body
                restApiException,
                // HTTP status code
                HttpStatus.BAD_REQUEST
        );
    }

    // @Valid 유효성 검사 에러 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestApiException> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessages = new StringBuilder();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorMessages.append(error.getField())
                        .append(" : ")
                        .append(error.getDefaultMessage())
                        .append(" ")
        );

        RestApiException restApiException = new RestApiException(errorMessages.toString(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(restApiException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NullPointerException.class})
    public ResponseEntity<RestApiException> nullPointerExceptionHandler(NullPointerException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(
                // HTTP body
                restApiException,
                // HTTP status code
                HttpStatus.NOT_FOUND
        );
    }

    //접근 권한이 없는 api에 접근을 시도할 경우 예외처리
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RestApiException> handleAccessDeniedException() {
        RestApiException restApiException = new RestApiException(
                ExceptionMessage.NOT_ALLOWED_API.getMessage(),  // 에러 메시지
                HttpStatus.FORBIDDEN.value()  // 상태 코드 (403)
        );
        return new ResponseEntity<>(restApiException, HttpStatus.FORBIDDEN);
    }
}
