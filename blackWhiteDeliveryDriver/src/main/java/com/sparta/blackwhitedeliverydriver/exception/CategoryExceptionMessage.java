package com.sparta.blackwhitedeliverydriver.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryExceptionMessage {
    CATEGORY_ID_NOT_FOUND("해당 카테고리ID를 찾을 수 없습니다."),
    CATEGORY_DUPLICATED("중복된 카테고리 이름이 있습니다."),
    CATEGORY_NOT_FOUND("해당 카테고리가 존재하지 않습니다.");
    private final String message;
}
