package com.sparta.blackwhitedeliverydriver.product.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductExceptionMessage {
    PRODUCT_NOT_FOUND("존재하지 않는 음식 정보입니다."),
    DUPLICATED_STORE_NAME("중복된 음식입니다.");
    private final String message;


}
