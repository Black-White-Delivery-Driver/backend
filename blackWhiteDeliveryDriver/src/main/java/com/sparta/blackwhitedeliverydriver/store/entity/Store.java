package com.sparta.blackwhitedeliverydriver.store.entity;

import com.sparta.blackwhitedeliverydriver.common.entity.BaseEntity;
import com.sparta.blackwhitedeliverydriver.user.entity.User;
import com.sparta.blackwhitedeliverydriver.store.dto.StoreRequestDto;
import com.sparta.blackwhitedeliverydriver.store.dto.StoreByMasterRequestDto;
import com.sparta.blackwhitedeliverydriver.common.security.UserDetailsImpl;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_stores")
public class Store extends BaseEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "store_id", updatable = false, nullable = false)
    private UUID storeId;
    @Column(name = "store_name", nullable = false, unique = true)
    private String storeName;
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    @Column(name = "status", nullable = false)
    private Boolean status = true; // 매장 운영 여부
    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;
    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;
    @Column(name = "rating", nullable = false)
    private Integer rating = 0; // 매장 평점 합산
    @Column(name = "review_cnt", nullable = false)
    private Integer reviewCnt = 0;
    @Column(name = "img_url")
    private String imgUrl;
    @Column(name = "zip_num", nullable = false)
    private String zipNum;
    @Column(name = "city", nullable = false)
    private String city;
    @Column(name = "district", nullable = false)
    private String district;
    @Column(name = "street_name", nullable = false)
    private String streetName;
    @Column(name = "street_number", nullable = false)
    private String streetNumber;
    @Column(name = "detail_addr", nullable = false)
    private String detailAddr;
    @Column(name = "store_intro", nullable = false)
    private String storeIntro;
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false; // 매장 승인 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_username", nullable = false)
    private User user;

    public static Store from(StoreRequestDto requestDto, User user) {
        return Store.builder()
                .storeName(requestDto.getStoreName())
                .phoneNumber(requestDto.getPhoneNumber())
                .status(true)
                .openTime(requestDto.getOpenTime())
                .closeTime(requestDto.getCloseTime())
                .imgUrl(requestDto.getImgUrl())
                .zipNum(requestDto.getZipNum())
                .city(requestDto.getCity())
                .district(requestDto.getDistrict())
                .streetName(requestDto.getStreetName())
                .streetNumber(requestDto.getStreetNumber())
                .detailAddr(requestDto.getDetailAddr())
                .storeIntro(requestDto.getStoreIntro())
                .isPublic(false)
                .rating(0)
                .reviewCnt(0)
                .user(user)
                .build();
    }

    public void update(StoreRequestDto requestDto) {
        this.storeName = requestDto.getStoreName();
        this.phoneNumber = requestDto.getPhoneNumber();
        this.openTime = requestDto.getOpenTime();
        this.closeTime = requestDto.getCloseTime();
        this.imgUrl = requestDto.getImgUrl();
        this.zipNum = requestDto.getZipNum();
        this.city = requestDto.getCity();
        this.district = requestDto.getDistrict();
        this.streetName = requestDto.getStreetName();
        this.streetNumber = requestDto.getStreetNumber();
        this.detailAddr = requestDto.getDetailAddr();
        this.storeIntro = requestDto.getStoreIntro();
    }

    public void updateRating(int rating) {
        this.rating += rating;
        this.reviewCnt++;
    }

    public void updateRating(int previousRating, int updatedRating) {
        this.rating = rating - previousRating + updatedRating;
    }

    public void updateByMaster(StoreByMasterRequestDto requestDto, UserDetailsImpl userDetails) {
        this.storeName = requestDto.getStoreName();
        this.phoneNumber = requestDto.getPhoneNumber();
        this.openTime = requestDto.getOpenTime();
        this.closeTime = requestDto.getCloseTime();
        this.imgUrl = requestDto.getImgUrl();
        this.zipNum = requestDto.getZipNum();
        this.city = requestDto.getCity();
        this.district = requestDto.getDistrict();
        this.streetName = requestDto.getStreetName();
        this.streetNumber = requestDto.getStreetNumber();
        this.detailAddr = requestDto.getDetailAddr();
        this.storeIntro = requestDto.getStoreIntro();
    }

    public void updatePublic(Boolean isPublic) {
        if(isPublic) {this.isPublic = false; }
        else {this.isPublic = true; }
    }
}
