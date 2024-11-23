package com.sparta.blackwhitedeliverydriver.bascket.repository;

import com.sparta.blackwhitedeliverydriver.bascket.entity.Basket;
import com.sparta.blackwhitedeliverydriver.user.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BasketRepository extends JpaRepository<Basket, UUID> {
    List<Basket> findAllByUser(User user);

    @Query("SELECT b FROM Basket b WHERE b.user = :user AND b.deletedDate IS NULL")
    List<Basket> findAllByUserAndNotDeleted(User user);

    @Query("SELECT b FROM Basket b WHERE b.user = :user AND b.deletedDate IS NULL")
    Page<Basket> findAllByUserAndNotDeleted(User user, Pageable pageable);

    @Query("SELECT b FROM Basket b WHERE b.product.name LIKE %:productName% AND b.user = :user AND b.deletedDate IS NULL")
    Page<Basket> findByProductNameContainingAndUserAndNotDeleted(@Param("productName") String productName, User user, Pageable pageable);
}
