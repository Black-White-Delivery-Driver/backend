package com.sparta.blackwhitedeliverydriver.store.dto;

import com.sparta.blackwhitedeliverydriver.model.store.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreResponseDto {
    private UUID storeId;
    private String storeName;
    private String phoneNumber;
    private LocalTime openTime;
    private LocalTime closeTime;
    private String imgUrl;
    private String zipNum;
    private String city;
    private String district;
    private String streetName;
    private String streetNumber;
    private String detailAddr;
    private String storeIntro;
    private String category;
    private double avgRating;

    public static StoreResponseDto from(Store store, String categoryNames) {
        return StoreResponseDto.builder()
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .phoneNumber(store.getPhoneNumber())
                .openTime(store.getOpenTime())
                .closeTime(store.getCloseTime())
                .imgUrl(store.getImgUrl())
                .zipNum(store.getZipNum())
                .city(store.getCity())
                .district(store.getDistrict())
                .streetName(store.getStreetName())
                .streetNumber(store.getStreetNumber())
                .detailAddr(store.getDetailAddr())
                .storeIntro(store.getStoreIntro())
                .category(categoryNames)
                .avgRating((double)store.getRating() / store.getReviewCnt())
                .build();
    }
}
