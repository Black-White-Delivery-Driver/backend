package com.sparta.blackwhitedeliverydriver.controller;

import com.sparta.blackwhitedeliverydriver.dto.SignupRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.SignupResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.UpdateUserRequestDto;
import com.sparta.blackwhitedeliverydriver.security.UserDetailsImpl;
import com.sparta.blackwhitedeliverydriver.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        // 사용자 등록 처리
        SignupResponseDto responseDto = userService.signup(requestDto);

        // 성공 응답으로 201 Created와 사용자 ID 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping("/")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 사용자 정보 업데이트
        SignupResponseDto responseDto = userService.updateUser(requestDto, userDetails.getId());

        // 성공 응답으로 200 OK와 사용자 ID 반환
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Secured({"ROLE_MANAGER", "ROLE_MASTER"})
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserRequestDto requestDto, @PathVariable Long userId) {
        // 사용자 정보 업데이트
        SignupResponseDto responseDto = userService.updateUser(requestDto, userId);

        // 성공 응답으로 200 OK와 사용자 ID 반환
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}