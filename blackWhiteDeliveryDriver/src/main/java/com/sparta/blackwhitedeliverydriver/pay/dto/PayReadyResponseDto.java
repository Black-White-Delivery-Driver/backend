package com.sparta.blackwhitedeliverydriver.pay.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PayReadyResponseDto {
    private String tid;
    private String next_redirect_pc_url; //pc redirect 할 url
    private LocalDateTime created_at; //언제 결제를 요청했는지
}