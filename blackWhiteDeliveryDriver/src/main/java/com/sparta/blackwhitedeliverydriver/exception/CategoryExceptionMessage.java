package com.sparta.blackwhitedeliverydriver.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryExceptionMessage {
    CATEGORY_ID_NOT_FOUND("해당 카테고리ID를 찾을 수 없습니다.");
    private final String message;
}
