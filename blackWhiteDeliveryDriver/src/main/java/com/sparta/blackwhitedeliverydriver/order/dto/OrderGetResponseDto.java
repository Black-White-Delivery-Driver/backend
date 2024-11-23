package com.sparta.blackwhitedeliverydriver.order.dto;

import com.sparta.blackwhitedeliverydriver.order.entity.Order;
import com.sparta.blackwhitedeliverydriver.order.entity.OrderStatusEnum;
import com.sparta.blackwhitedeliverydriver.order.entity.OrderTypeEnum;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderGetResponseDto {
    private UUID orderId;
    private UUID storeId;
    private String username;
    private OrderStatusEnum status;
    private OrderTypeEnum type;
    private Integer finalPay;
    private Integer discountRate;
    private Integer discountAmount;

    public static OrderGetResponseDto fromOrder(Order order) {
        return OrderGetResponseDto.builder()
                .orderId(order.getId())
                .storeId(order.getStore().getStoreId())
                .username(order.getUser().getUsername())
                .status(order.getStatus())
                .type(order.getType())
                .finalPay(order.getFinalPay())
                .discountRate(order.getDiscountRate())
                .discountAmount(order.getDiscountAmount())
                .build();
    }
}
