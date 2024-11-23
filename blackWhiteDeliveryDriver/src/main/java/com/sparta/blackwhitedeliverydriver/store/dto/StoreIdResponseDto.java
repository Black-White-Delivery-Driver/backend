package com.sparta.blackwhitedeliverydriver.store.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreIdResponseDto {
    private UUID storeId;

    public StoreIdResponseDto(UUID storeId){
        this.storeId = storeId;
    }
}
