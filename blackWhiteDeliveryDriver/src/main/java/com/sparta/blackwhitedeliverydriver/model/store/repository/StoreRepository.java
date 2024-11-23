package com.sparta.blackwhitedeliverydriver.model.store.repository;

import com.sparta.blackwhitedeliverydriver.model.store.entity.Store;
import com.sparta.blackwhitedeliverydriver.model.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, UUID>{

    Optional<Store> findByStoreName(@NotBlank String storeName);

    Optional<Store> findByStoreIdAndDeletedDateIsNullAndDeletedByIsNull(UUID storeId);

    Page<Store> findAllByStoreNameContainingAndDeletedDateIsNullAndDeletedByIsNull(String storeName, Pageable pageable);

    Page<Store> findAllByDeletedDateIsNullAndDeletedByIsNullAndIsPublicTrue(Pageable pageable);

    Optional<Store> findByStoreIdAndDeletedDateIsNullAndDeletedByIsNullAndIsPublicTrue(UUID storeId);

    Optional<Store> findByStoreIdAndIsPublicTrue(UUID storeId);

    Page<Store> findAllByUserAndDeletedDateIsNullAndDeletedByIsNullAndIsPublicTrue(User user, Pageable pageable);
}
