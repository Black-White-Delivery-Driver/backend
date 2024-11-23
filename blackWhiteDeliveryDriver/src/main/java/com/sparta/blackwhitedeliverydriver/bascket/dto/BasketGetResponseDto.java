package com.sparta.blackwhitedeliverydriver.bascket.dto;

import com.sparta.blackwhitedeliverydriver.model.bascket.entity.Basket;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Builder
public class BasketGetResponseDto {
    private UUID basketId;
    private String username;
    private UUID storeId;
    private String storeName;
    private UUID productId;
    private String productName;
    private Integer quantity;

    public static BasketGetResponseDto fromBasket(Basket basket) {
        return BasketGetResponseDto
                .builder()
                .basketId(basket.getId())
                .username(basket.getUser().getUsername())
                .storeId(basket.getStore().getStoreId())
                .storeName(basket.getStore().getStoreName())
                .productId(basket.getProduct().getProductId())
                .productName(basket.getProduct().getName())
                .quantity(basket.getQuantity())
                .build();
    }
}
