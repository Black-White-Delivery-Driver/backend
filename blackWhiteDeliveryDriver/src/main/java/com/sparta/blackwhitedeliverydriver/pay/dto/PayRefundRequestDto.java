package com.sparta.blackwhitedeliverydriver.pay.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PayRefundRequestDto {
    @NotNull
    private UUID orderId;
}
