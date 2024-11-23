package com.sparta.blackwhitedeliverydriver.model.product.repository;

import com.sparta.blackwhitedeliverydriver.model.product.entity.Product;
import com.sparta.blackwhitedeliverydriver.model.store.entity.Store;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findAllByStore(Store store);

    Optional<Product> findByNameAndStoreStoreId(@NotBlank String productName, UUID storeId);

    Page<Product> findAllByStoreAndDeletedDateIsNullAndDeletedByIsNull(Store store, Pageable pageable);
}
