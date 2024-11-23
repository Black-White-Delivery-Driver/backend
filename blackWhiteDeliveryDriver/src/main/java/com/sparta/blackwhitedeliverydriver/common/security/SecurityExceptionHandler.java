package com.sparta.blackwhitedeliverydriver.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.blackwhitedeliverydriver.common.exception.RestApiException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityExceptionHandler {
    public static void jwtExceptionHandler(HttpServletResponse response, String errorMessage, int statusCode) {
        response.setStatus(statusCode);  // 상태 코드 설정
        response.setContentType("application/json");  // 응답 형식 설정
        response.setCharacterEncoding("UTF-8");  // 문자 인코딩 설정
        try {
            RestApiException exception = new RestApiException(errorMessage, statusCode);
            String json = new ObjectMapper().writeValueAsString(exception);

            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
