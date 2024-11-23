package com.sparta.blackwhitedeliverydriver.order.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderTypeEnum {
    ONLINE,
    OFFLINE;

    public String getType() {
        return this.name();
    }

    @JsonCreator
    public static OrderTypeEnum fromString(String type) {
        return OrderTypeEnum.valueOf(type.toUpperCase()); // 대소문자 구분하지 않음
    }

    @JsonValue
    public String toValue() {
        return this.name(); // name()은 enum의 이름을 반환 (PENDING, ACCEPTED 등)
    }
}
