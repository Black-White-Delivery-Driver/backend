package com.sparta.blackwhitedeliverydriver.product.dto;

import com.sparta.blackwhitedeliverydriver.product.entity.Product;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    private UUID productId;
    private String name;
    private Integer price;
    private String imgUrl;
    private String productIntro;

    public static ProductResponseDto from(Product product) {
        return ProductResponseDto.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .imgUrl(product.getImgUrl())
                .productIntro(product.getProductIntro())
                .build();
    }
}
