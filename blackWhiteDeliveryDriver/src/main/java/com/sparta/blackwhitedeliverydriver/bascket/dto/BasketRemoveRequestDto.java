package com.sparta.blackwhitedeliverydriver.bascket.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BasketRemoveRequestDto {
    private UUID basketId;
}
