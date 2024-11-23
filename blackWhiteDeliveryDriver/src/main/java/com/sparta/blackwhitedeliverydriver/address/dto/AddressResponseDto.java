package com.sparta.blackwhitedeliverydriver.address.dto;

import com.sparta.blackwhitedeliverydriver.model.address.entity.Address;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponseDto {
    private UUID id;

    private String zipNum;

    private String city;

    private String district;

    private String streetName;

    private String streetNum;

    private String detailAddr;

    private String requestDetails;

    // 정적 팩토리 메서드 + 빌더 패턴
    public static AddressResponseDto from(Address address) {
        return AddressResponseDto.builder()
                .id(address.getId())  // id 포함
                .zipNum(address.getZipNum())
                .city(address.getCity())
                .district(address.getDistrict())
                .streetName(address.getStreetName())
                .streetNum(address.getStreetNum())
                .detailAddr(address.getDetailAddr())
                .requestDetails(address.getRequestDetails())
                .build();
    }
}
