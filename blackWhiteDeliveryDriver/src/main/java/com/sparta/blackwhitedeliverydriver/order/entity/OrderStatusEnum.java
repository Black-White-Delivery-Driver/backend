package com.sparta.blackwhitedeliverydriver.order.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatusEnum {
    CREATE,         // 주문 생성 상태
    PENDING,       // 주문 대기 상태
    ACCEPTED,     // 주문 수락 상태
    REJECTED,     // 주문 거절 상태
    COMPLETED,
    CANCEL;   // 주문 완료 상태

    public String getStatus() {
        return this.name();
    }

    @JsonCreator
    public static OrderStatusEnum fromString(String status) {
        return OrderStatusEnum.valueOf(status.toUpperCase()); // 대소문자 구분하지 않음
    }

    @JsonValue
    public String toValue() {
        return this.name(); // name()은 enum의 이름을 반환 (PENDING, ACCEPTED 등)
    }
}