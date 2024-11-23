package com.sparta.blackwhitedeliverydriver.address.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequestDto {
    @NotBlank
    private String zipNum;
    @NotBlank
    private String city;
    @NotBlank
    private String district;
    @NotBlank
    private String streetName;
    @NotBlank
    private String streetNum;
    @NotBlank
    private String detailAddr;

    private String requestDetails;
}
