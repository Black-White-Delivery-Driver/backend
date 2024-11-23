package com.sparta.blackwhitedeliverydriver.order.repository;

import com.sparta.blackwhitedeliverydriver.order.entity.Order;
import com.sparta.blackwhitedeliverydriver.store.entity.Store;
import com.sparta.blackwhitedeliverydriver.user.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllByUser(User user);
    Optional<Order> findByTid(String tid);
    @Query("SELECT o FROM Order o WHERE o.user = :user AND o.deletedDate IS NULL")
    Page<Order> findAllByUserAndNotDeleted(User user, Pageable pageable);
    @Query("SELECT o FROM Order o WHERE o.store = :store AND o.deletedDate IS NULL")
    Page<Order> findAllByStoreAndNotDeleted(Store store, Pageable pageable);
    @Query("SELECT o FROM Order o WHERE o.store.storeName LIKE %:storeName%") // 관리자용 deletedAt이 null이 아닌 것도 포함
    Page<Order> findByStoreNameContaining(@Param("storeName") String storeName, Pageable pageable);
    @Query("SELECT o FROM Order o WHERE o.user = :user AND o.status = 'CREATE' AND o.deletedDate IS NULL")
    Optional<Order> findActiveOrderByUser(User user);
}
