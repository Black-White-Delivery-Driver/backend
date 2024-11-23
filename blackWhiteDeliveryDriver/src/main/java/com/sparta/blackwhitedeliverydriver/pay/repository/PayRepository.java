package com.sparta.blackwhitedeliverydriver.pay.repository;

import com.sparta.blackwhitedeliverydriver.order.entity.Order;
import com.sparta.blackwhitedeliverydriver.pay.entity.Pay;
import com.sparta.blackwhitedeliverydriver.user.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PayRepository extends JpaRepository<Pay, UUID> {
    Optional<Pay> findByOrder(Order order);

    @Query("select p from  Pay p where p.order.user.username = :username")
    List<Pay> findAllByUser(@Param("username") String username);

    @Query("select p from  Pay p where p.order.user = :user")
    Page<Pay> findAllByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT p FROM Pay p WHERE p.order.store.storeName LIKE %:storeName%")
    Page<Pay> findByStoreNameContaining(@Param("storeName") String storeName, Pageable pageable);
}
