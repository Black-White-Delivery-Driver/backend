package com.sparta.blackwhitedeliverydriver.bascket.dto;

import com.sparta.blackwhitedeliverydriver.bascket.entity.Basket;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class BasketResponseDto {
    private UUID basketId;

    public static BasketResponseDto fromBasket(Basket basket) {
        return BasketResponseDto.builder().basketId(basket.getId()).build();
    }
}
