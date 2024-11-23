package com.sparta.blackwhitedeliverydriver.order.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderExceptionMessage {
    ORDER_NOT_FOUND("주문 내역을 찾을 수 없습니다."),
    ORDER_USER_NOT_EQUALS("주문서의 유저가 아닙니다."),
    ORDER_UNABLE_DELETE_STATUS("주문을 취소할 수 있는 상태가 아닙니다."),
    ORDER_UNABLE_PAY_STATUS("결제를 할 수 있는 주문 상태가 아닙니다."),
    ORDER_UNABLE_UPDATE("주문 상태를 변경할 수 없습니다."),
    ORDER_ALREADY_EXIST("생성한 주문이 이미 존재합니다.");
    private final String message;
}
