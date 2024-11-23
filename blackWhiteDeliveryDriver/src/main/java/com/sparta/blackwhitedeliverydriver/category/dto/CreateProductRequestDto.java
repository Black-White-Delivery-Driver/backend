package com.sparta.blackwhitedeliverydriver.category.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Getter;

@Getter
public class CreateProductRequestDto {
    @NotBlank
    private UUID storeId;
    @NotBlank
    private String productName;
    @NotBlank
    private Integer price;
    private String imgUrl;
    @NotBlank
    private String productIntro;
}
