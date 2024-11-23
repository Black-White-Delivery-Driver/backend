package com.sparta.blackwhitedeliverydriver.pay.dto;

import com.sparta.blackwhitedeliverydriver.order.dto.OrderGetDetailResponseDto;
import com.sparta.blackwhitedeliverydriver.model.order.entity.OrderProduct;
import com.sparta.blackwhitedeliverydriver.model.pay.entity.Pay;
import com.sparta.blackwhitedeliverydriver.model.pay.entity.PayStatusEnum;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayGetDetailResponseDto {
    private UUID payId;
    private OrderGetDetailResponseDto order;
    private String itemName;
    private PayStatusEnum payStatus;
    private Integer payAmount;
    private Integer refundAmount;
    private LocalDateTime approvedAt;
    private LocalDateTime canceledAt;

    public static PayGetDetailResponseDto ofPayAndOrderProducts(Pay pay, List<OrderProduct> products) {
        return PayGetDetailResponseDto.builder()
                .payId(pay.getId())
                .order(OrderGetDetailResponseDto.of(pay.getOrder(), products))
                .itemName(pay.getItemName())
                .payStatus(pay.getPayStatus())
                .payAmount(pay.getPayAmount())
                .refundAmount(pay.getRefundAmount())
                .approvedAt(pay.getApprovedAt())
                .canceledAt(pay.getCanceledAt())
                .build();
    }
}