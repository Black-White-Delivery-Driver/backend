package com.sparta.blackwhitedeliverydriver.order.dto;

import com.sparta.blackwhitedeliverydriver.order.entity.OrderStatusEnum;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateRequestDto {
    @NotNull
    private UUID orderId;
    @NotNull
    private OrderStatusEnum status;
}
