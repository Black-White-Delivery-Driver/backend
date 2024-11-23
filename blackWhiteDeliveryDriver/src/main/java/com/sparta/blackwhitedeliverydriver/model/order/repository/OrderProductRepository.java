package com.sparta.blackwhitedeliverydriver.model.order.repository;

import com.sparta.blackwhitedeliverydriver.model.order.entity.Order;
import com.sparta.blackwhitedeliverydriver.model.order.entity.OrderProduct;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderProductRepository extends JpaRepository<OrderProduct, UUID> {
    @Query("SELECT op FROM OrderProduct op WHERE op.order = :order AND op.deletedDate IS NULL")
    List<OrderProduct> findAllByOrderAndNotDeleted(@Param("order") Order order);
}
