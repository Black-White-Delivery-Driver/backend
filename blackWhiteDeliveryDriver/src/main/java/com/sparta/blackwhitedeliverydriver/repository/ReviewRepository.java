package com.sparta.blackwhitedeliverydriver.repository;

import com.sparta.blackwhitedeliverydriver.entity.Review;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findAllByOrderStoreStoreIdAndDeletedByIsNullAndDeletedDateIsNull(UUID storeId, Pageable pageable);
    Page<Review> findAllByOrderUserUsernameAndDeletedByIsNullAndDeletedDateIsNull(String username, Pageable pageable);
}
