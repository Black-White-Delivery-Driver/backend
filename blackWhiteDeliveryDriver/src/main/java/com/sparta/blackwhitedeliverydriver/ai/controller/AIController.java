package com.sparta.blackwhitedeliverydriver.ai.controller;

import com.sparta.blackwhitedeliverydriver.ai.dto.AIRequestDto;
import com.sparta.blackwhitedeliverydriver.ai.dto.AIResponseDto;
import com.sparta.blackwhitedeliverydriver.common.security.UserDetailsImpl;
import com.sparta.blackwhitedeliverydriver.ai.service.AIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/ai")
public class AIController {

    private final AIService aiService;

    @Secured("ROLE_OWNER")
    @PostMapping("/intro")
    public ResponseEntity<?> recommendProductNames(@Valid @RequestBody AIRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        AIResponseDto aiResponseDto = aiService.recommendProductNames(requestDto, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(aiResponseDto);
    }
}
