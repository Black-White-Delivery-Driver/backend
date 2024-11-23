package com.sparta.blackwhitedeliverydriver.pay.dto;

import com.sparta.blackwhitedeliverydriver.model.pay.entity.Pay;
import com.sparta.blackwhitedeliverydriver.model.pay.entity.PayStatusEnum;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayGetResponseDto {
    private UUID payId;
    private String itemName;
    private PayStatusEnum payStatus;
    private Integer payAmount;
    private Integer refundAmount;
    private LocalDateTime approvedAt;
    private LocalDateTime canceledAt;

    public static PayGetResponseDto fromPay(Pay pay) {
        return PayGetResponseDto.builder()
                .payId(pay.getId())
                .itemName(pay.getItemName())
                .payStatus(pay.getPayStatus())
                .payAmount(pay.getPayAmount())
                .refundAmount(pay.getRefundAmount())
                .approvedAt(pay.getApprovedAt())
                .canceledAt(pay.getCanceledAt())
                .build();
    }

}
