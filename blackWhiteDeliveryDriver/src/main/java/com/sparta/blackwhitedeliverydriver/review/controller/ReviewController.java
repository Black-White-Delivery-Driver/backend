package com.sparta.blackwhitedeliverydriver.review.controller;

import com.sparta.blackwhitedeliverydriver.review.dto.ReviewIdResponseDto;
import com.sparta.blackwhitedeliverydriver.review.dto.ReviewRequestDto;
import com.sparta.blackwhitedeliverydriver.review.dto.ReviewResponseDto;
import com.sparta.blackwhitedeliverydriver.common.security.UserDetailsImpl;
import com.sparta.blackwhitedeliverydriver.review.service.ReviewService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{orderId}")
    public ResponseEntity<ReviewIdResponseDto> createReview(@Valid @RequestBody ReviewRequestDto requestDto,
                                                            @PathVariable UUID orderId) {
        ReviewIdResponseDto responseDto = reviewService.createReview(requestDto, orderId);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/store")
    public ResponseEntity<Page<ReviewResponseDto>> getAllReviewsByStoreId(
            @RequestParam UUID storeId,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc) {

        Page<ReviewResponseDto> responseDtos = reviewService.getAllReviewsByStoreId(
                storeId, page - 1, size, sortBy, isAsc);

        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }

    @GetMapping("/user")
    public ResponseEntity<Page<ReviewResponseDto>> getAllReviewsByUsername(
            @RequestParam String username,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc) {

        Page<ReviewResponseDto> responseDtos = reviewService.getAllReviewsByUsername(
                username, page - 1, size, sortBy, isAsc);

        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> getReview(@PathVariable UUID reviewId) {
        ReviewResponseDto responseDto = reviewService.getReview(reviewId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Secured({"ROLE_CUSTOMER", "ROLE_MANAGER", "ROLE_MASTER"})
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewIdResponseDto> updateReview(
            @PathVariable UUID reviewId,
            @Valid @RequestBody ReviewRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        ReviewIdResponseDto responseDto = reviewService.updateReview(reviewId, requestDto, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Secured({"ROLE_CUSTOMER", "ROLE_MANAGER", "ROLE_MASTER"})
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ReviewIdResponseDto> deleteReview(
            @PathVariable UUID reviewId, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        ReviewIdResponseDto responseDto = reviewService.deleteReview(reviewId, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
