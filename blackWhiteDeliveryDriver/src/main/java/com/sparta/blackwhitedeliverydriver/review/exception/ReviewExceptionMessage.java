package com.sparta.blackwhitedeliverydriver.review.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewExceptionMessage {
    REVIEW_NOT_FOUND("해당 리뷰가 존재하지 않습니다."),
    REVIEW_DELETED("삭제된 리뷰입니다.");
    private final String message;
}
