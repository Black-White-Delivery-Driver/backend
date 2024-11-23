package com.sparta.blackwhitedeliverydriver.user.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRoleEnum {
    CUSTOMER(Authority.CUSTOMER),  // 손님 권한
    OWNER(Authority.OWNER),  // 점주 권한
    MANAGER(Authority.MANAGER), //매니저 권한
    MASTER(Authority.MASTER); //관리자 권한

    private final String authority;

    UserRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String CUSTOMER = "ROLE_CUSTOMER";
        public static final String OWNER = "ROLE_OWNER";
        public static final String MANAGER = "ROLE_MANAGER";
        public static final String MASTER = "ROLE_MASTER";
    }

    @JsonCreator
    public static UserRoleEnum fromString(String role) {
        return UserRoleEnum.valueOf(role.toUpperCase()); // 대소문자 구분을 하지 않음
    }

    @JsonValue
    public String toValue() {
        return this.name(); // name()은 enum의 이름을 반환 (CUSTOMER, OWNER 등)
    }
}
