package com.sparta.blackwhitedeliverydriver.product.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ProductRequestDto {
    @NotBlank
    private String name;
    @NotBlank
    private Integer price;
    @NotBlank
    private String imgUrl;
    @NotBlank
    private String productIntro;
}
