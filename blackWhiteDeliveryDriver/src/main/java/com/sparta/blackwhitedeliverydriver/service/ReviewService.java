package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.dto.ReviewIdResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.ReviewRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.ReviewResponseDto;
import com.sparta.blackwhitedeliverydriver.entity.Order;
import com.sparta.blackwhitedeliverydriver.entity.Review;
import com.sparta.blackwhitedeliverydriver.entity.Store;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.exception.ExceptionMessage;
import com.sparta.blackwhitedeliverydriver.exception.OrderExceptionMessage;
import com.sparta.blackwhitedeliverydriver.exception.ReviewExceptionMessage;
import com.sparta.blackwhitedeliverydriver.exception.StoreExceptionMessage;
import com.sparta.blackwhitedeliverydriver.repository.OrderRepository;
import com.sparta.blackwhitedeliverydriver.repository.ReviewRepository;
import com.sparta.blackwhitedeliverydriver.repository.StoreRepository;
import com.sparta.blackwhitedeliverydriver.repository.UserRepository;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewIdResponseDto createReview(ReviewRequestDto requestDto, UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NullPointerException(OrderExceptionMessage.ORDER_NOT_FOUND.getMessage()));

        Store store = storeRepository.findById(order.getStore().getStoreId())
                .orElseThrow(() -> new NullPointerException(StoreExceptionMessage.STORE_NOT_FOUND.getMessage()));

        // 리뷰 총점 업데이트 및 리뷰수 +1
        store.updateRating(requestDto.getRating());
        storeRepository.save(store);

        Review review = Review.from(requestDto, order);
        reviewRepository.save(review);

        return new ReviewIdResponseDto(review.getId());
    }

    public Page<ReviewResponseDto> getAllReviewsByStoreId(UUID storeId, int page, int size, String sortBy, boolean isAsc) {
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        // 페이징 처리
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Review> reviewPage = reviewRepository.findAllByOrderStoreStoreIdAndDeletedByIsNullAndDeletedDateIsNull(storeId, pageable);

        return reviewPage.map(ReviewResponseDto::from);
    }

    public Page<ReviewResponseDto> getAllReviewsByUsername(String username, int page, int size, String sortBy, boolean isAsc) {
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        // 페이징 처리
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Review> reviewPage = reviewRepository.findAllByOrderUserUsernameAndDeletedByIsNullAndDeletedDateIsNull(username, pageable);

        return reviewPage.map(ReviewResponseDto::from);
    }


    public ReviewResponseDto getReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NullPointerException(ReviewExceptionMessage.REVIEW_NOT_FOUND.getMessage()));

        checkDeletedReview(review);

        return ReviewResponseDto.from(review);
    }

    @Transactional
    public ReviewIdResponseDto updateReview(UUID reviewId, @Valid ReviewRequestDto requestDto, String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NullPointerException(ReviewExceptionMessage.REVIEW_NOT_FOUND.getMessage()));

        Store store = storeRepository.findById(review.getOrder().getStore().getStoreId())
                .orElseThrow(() -> new NullPointerException(StoreExceptionMessage.STORE_NOT_FOUND.getMessage()));

        checkDeletedReview(review);
        if (user.getRole() == UserRoleEnum.CUSTOMER) {
            checkCreatedBy(review, user.getUsername());
        }

        //평점 업데이트, 기존 값은 빼고 업데이트 한 값을 더한다.
        store.updateRating(review.getRating(), requestDto.getRating());
        storeRepository.save(store);

        review.update(requestDto.getContents(), requestDto.getRating());
        reviewRepository.save(review);

        return new ReviewIdResponseDto(review.getId());
    }

    @Transactional
    public ReviewIdResponseDto deleteReview(UUID reviewId, String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NullPointerException(ReviewExceptionMessage.REVIEW_NOT_FOUND.getMessage()));

        Store store = storeRepository.findById(review.getOrder().getStore().getStoreId())
                .orElseThrow(() -> new NullPointerException(StoreExceptionMessage.STORE_NOT_FOUND.getMessage()));

        checkDeletedReview(review);
        if (user.getRole() == UserRoleEnum.CUSTOMER) {
            checkCreatedBy(review, user.getUsername());
        }

        store.updateRating(review.getRating(), 0);
        storeRepository.save(store);

        review.setDeletedBy(user.getUsername());
        review.setDeletedDate(LocalDateTime.now());
        reviewRepository.save(review);

        return new ReviewIdResponseDto(review.getId());
    }

    private void checkDeletedReview(Review review) {
        if (review.getDeletedBy() != null || review.getDeletedDate() != null) {
            throw new IllegalArgumentException(ReviewExceptionMessage.REVIEW_DELETED.getMessage());
        }
    }

    private void checkCreatedBy(Review review, String username) {
        if (!review.getCreatedBy().equals(username)) {
            throw new AccessDeniedException(ExceptionMessage.NOT_ALLOWED_API.getMessage());
        }
    }
}
