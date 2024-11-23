package com.sparta.blackwhitedeliverydriver.product.entity;

import com.sparta.blackwhitedeliverydriver.category.dto.CreateProductRequestDto;
import com.sparta.blackwhitedeliverydriver.common.entity.BaseEntity;
import com.sparta.blackwhitedeliverydriver.product.dto.ProductRequestDto;
import com.sparta.blackwhitedeliverydriver.common.security.UserDetailsImpl;
import com.sparta.blackwhitedeliverydriver.store.entity.Store;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "p_product")
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "product_id", updatable = false)
    private UUID productId;

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "price", nullable = false)
    private Integer price;
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;
    @Column(name = "img_url", nullable = false)
    private String imgUrl;
    @Column(name = "product_intro", nullable = false)
    private String productIntro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_store_id", nullable = false)
    private Store store;

    public static Product from(CreateProductRequestDto requestDto, Store store) {
        return Product.builder()
                .name(requestDto.getProductName())
                .price(requestDto.getPrice())
                .isPublic(true)
                .imgUrl(requestDto.getImgUrl())
                .productIntro(requestDto.getProductIntro())
                .store(store)
                .build();
    }

    public void update(ProductRequestDto requestDto, UserDetailsImpl userDetails) {
        this.name = requestDto.getName();
        this.price = requestDto.getPrice();
        this.imgUrl = requestDto.getImgUrl();
        this.productIntro = requestDto.getProductIntro();
    }
}
