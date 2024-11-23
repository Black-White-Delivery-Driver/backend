package com.sparta.blackwhitedeliverydriver.pay.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PayExceptionMessage {
    PAY_NOT_FOUND("주문 내역을 찾을 수 없습니다."),
    PAY_OFFLINE_TYPE("오프라인 결제 주문 건으로 온라인 결제를 할 수 없습니다."),
    PAY_UNABLE("환불를 할 수 없습니다."),
    PAY_REFUND_TIME_EXCEEDED("결제 시간 5분이 초과되어 환불를 할 수 없습니다.");
    private final String message;
}

