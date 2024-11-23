package com.sparta.blackwhitedeliverydriver.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreByMasterRequestDto {
    @NotBlank
    private String username;
    @NotBlank
    private String storeName;
    @NotBlank
    @Pattern(regexp = "^[0-9]{11}$")
    private String phoneNumber;
    private LocalTime openTime;
    private LocalTime closeTime;
    private String imgUrl;
    @NotBlank
    private String zipNum;
    @NotBlank
    private String city;
    @NotBlank
    private String district;
    @NotBlank
    private String streetName;
    @NotBlank
    private String streetNumber;
    @NotBlank
    private String detailAddr;
    @NotBlank
    private String storeIntro;

    private String category;
}
