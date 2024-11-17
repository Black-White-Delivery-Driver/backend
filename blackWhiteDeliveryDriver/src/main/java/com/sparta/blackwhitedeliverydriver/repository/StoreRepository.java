package com.sparta.blackwhitedeliverydriver.repository;

import com.sparta.blackwhitedeliverydriver.entity.Store;
import com.sparta.blackwhitedeliverydriver.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, UUID>{

    Page<Store> findAllByUser(User user, Pageable pageable);

    Page<Store> findAllByStoreNameContaining(String storeName, Pageable pageable);

    Optional<Store> findByStoreName(@NotBlank String storeName);

}
