package com.sparta.blackwhitedeliverydriver.model.review.repository;

import com.sparta.blackwhitedeliverydriver.model.review.entity.Review;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findAllByOrderStoreStoreIdAndDeletedByIsNullAndDeletedDateIsNull(UUID storeId, Pageable pageable);
    Page<Review> findAllByOrderUserUsernameAndDeletedByIsNullAndDeletedDateIsNull(String username, Pageable pageable);
}
