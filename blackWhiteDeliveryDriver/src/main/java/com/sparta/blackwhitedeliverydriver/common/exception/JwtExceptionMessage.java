package com.sparta.blackwhitedeliverydriver.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtExceptionMessage {
    INVALID_SIGNATURE("Invalid JWT signature, 유효하지 않은 JWT 서명입니다."),
    EXPIRED_TOKEN("Expired JWT token, 만료된 JWT token 입니다."),
    UNSUPPORTED_TOKEN("Unsupported JWT token, 지원되지 않는 JWT 토큰입니다."),
    CLAIM_IS_EMPTY("JWT claims is empty, 잘못된 JWT 토큰입니다.");

    private final String message;
}
