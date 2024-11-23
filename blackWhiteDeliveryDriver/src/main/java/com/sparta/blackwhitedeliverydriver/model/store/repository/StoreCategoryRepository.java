package com.sparta.blackwhitedeliverydriver.model.store.repository;

import com.sparta.blackwhitedeliverydriver.model.store.entity.StoreCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreCategoryRepository extends JpaRepository<StoreCategory, UUID> {
    List<StoreCategory> findAllByStoreStoreId(UUID storeId);

    Optional<StoreCategory> findByStoreStoreIdAndCategoryCategoryId(UUID storeId, UUID categoryId);

    void deleteAllByStoreStoreId(UUID storeId);
}
