package com.sparta.blackwhitedeliverydriver.order.dto;

import com.sparta.blackwhitedeliverydriver.model.order.entity.OrderTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderAddRequestDto {
    @NotNull
    private OrderTypeEnum type;
}
