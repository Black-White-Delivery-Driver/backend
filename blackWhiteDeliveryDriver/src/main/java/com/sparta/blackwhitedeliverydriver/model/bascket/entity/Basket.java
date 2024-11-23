package com.sparta.blackwhitedeliverydriver.model.bascket.entity;

import com.sparta.blackwhitedeliverydriver.bascket.dto.BasketAddRequestDto;
import com.sparta.blackwhitedeliverydriver.common.entity.BaseEntity;
import com.sparta.blackwhitedeliverydriver.model.order.entity.OrderProduct;
import com.sparta.blackwhitedeliverydriver.model.product.entity.Product;
import com.sparta.blackwhitedeliverydriver.model.user.entity.User;
import com.sparta.blackwhitedeliverydriver.model.store.entity.Store;
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
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_basket")
public class Basket extends BaseEntity {
    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    public static Basket ofUserAndOrderProduct(User user, Product product, OrderProduct orderProduct) {
        return Basket.builder()
                .product(product)
                .store(orderProduct.getOrder().getStore())
                .quantity(orderProduct.getQuantity())
                .user(user)
                .build();
    }

    public static Basket ofUserAndStoreAndRequest(User user, Store store, Product product,
                                                  BasketAddRequestDto requestDto) {
        return Basket.builder()
                .product(product)
                .store(store)
                .quantity(requestDto.getQuantity())
                .user(user)
                .build();
    }

    public void updateBasketOfQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void softDelete(String username, LocalDateTime deletedAt) {
        this.setDeletedBy(username);
        this.setDeletedDate(deletedAt);
    }

}
