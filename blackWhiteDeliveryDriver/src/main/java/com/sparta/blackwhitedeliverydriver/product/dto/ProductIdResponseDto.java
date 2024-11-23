package com.sparta.blackwhitedeliverydriver.product.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductIdResponseDto {
    private UUID productId;

    public ProductIdResponseDto(UUID productId) {
        this.productId = productId;
    }
}
