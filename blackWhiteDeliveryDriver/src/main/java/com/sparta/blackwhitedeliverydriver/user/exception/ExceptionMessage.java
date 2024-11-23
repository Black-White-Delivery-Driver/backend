package com.sparta.blackwhitedeliverydriver.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {
    NOT_ALLOWED_METHOD("허용되지 않은 메서드 호출입니다."),
    LOGIN_NOT_FOUND("로그인 정보가 없습니다."),
    ALREADY_LOGGED_IN("이미 로그인된 사용자입니다."),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    USER_DELETED("탈퇴한 회원입니다."),
    USER_NOT_PUBLIC("회원의 프로필이 비공개 상태입니다."),
    DUPLICATED_USERNAME("중복된 사용자가 존재합니다."),
    DUPLICATED_EMAIL("중복된 이메일이 존재합니다."),
    DUPLICATED_PHONENUMBER("중복된 전화번호가 존재합니다."),
    NOT_ALLOEWD_ROLE("일반 사용자는 CUSTOMER 또는 OWNER로만 가입할 수 있습니다."),
    NOT_ALLOWED_API("접근 권한이 없습니다."),
    ADDRESS_NOT_FOUND("해당 주소가 존재하지 않습니다."),
    ADDRESS_DELETED("삭제된 주소입니다."),
    CURRNET_ADDRESS_NOT_FOUND("현재(기본) 배송지로 지정한 배송지가 없습니다.");
    private final String message;
}
