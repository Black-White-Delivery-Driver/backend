package com.sparta.blackwhitedeliverydriver.bascket.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class BasketAddRequestDto {
    @NotNull
    private UUID productId;
    @NotNull
    @Min(value = 1)
    @Max(value = 99)
    private Integer quantity;
}
