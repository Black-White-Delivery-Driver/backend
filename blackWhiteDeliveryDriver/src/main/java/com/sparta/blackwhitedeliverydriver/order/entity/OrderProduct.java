package com.sparta.blackwhitedeliverydriver.order.entity;

import com.sparta.blackwhitedeliverydriver.bascket.entity.Basket;
import com.sparta.blackwhitedeliverydriver.common.entity.BaseEntity;
import com.sparta.blackwhitedeliverydriver.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "p_order_product")
public class OrderProduct extends BaseEntity {
    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer price;

    public static OrderProduct of(Basket basket, Product product, Order order) {
        return OrderProduct.builder()
                .product(product)
                .order(order)
                .quantity(basket.getQuantity())
                .price(product.getPrice())
                .build();
    }

    public void softDelete(String username, LocalDateTime deletedAt) {
        this.setDeletedBy(username);
        this.setDeletedDate(deletedAt);
    }

}
