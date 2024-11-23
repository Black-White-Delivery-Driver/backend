package com.sparta.blackwhitedeliverydriver.bascket.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BasketExceptionMessage {
    //장바구니
    BASKET_NOT_FOUND("장바구니를 찾을 수 없습니다."),
    BASKET_USER_NOT_EQUALS("장바구니 유저가 아닙니다."),
    BASKET_COUNT_ZERO("장바구니에 담긴 상품이 없습니다."),
    BASKET_DUPLICATED("장바구니에 담긴 상품입니다."),
    BASKET_DIFFERENT_STORE("장바구니에 담긴 상품과 다른 지점 상품입니다."),
    BASKET_DELETE("삭제된 장바구니입니다.");
    private final String message;
}
