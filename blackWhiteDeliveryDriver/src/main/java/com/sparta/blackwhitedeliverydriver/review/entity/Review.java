package com.sparta.blackwhitedeliverydriver.review.entity;

import com.sparta.blackwhitedeliverydriver.common.entity.BaseEntity;
import com.sparta.blackwhitedeliverydriver.order.entity.Order;
import com.sparta.blackwhitedeliverydriver.review.dto.ReviewRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "p_review")
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "contents", nullable = false)
    private String contents;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Builder
    public Review(Order order, String contents, Integer rating) {
        this.order = order;
        this.contents = contents;
        this.rating = rating;
    }

    public static Review from(ReviewRequestDto requestDto, Order order) {
        return Review.builder()
                .order(order)
                .contents(requestDto.getContents())
                .rating(requestDto.getRating())
                .build();
    }

    public void update(String contents, Integer rating) {
        this.contents = contents;
        this.rating = rating;
    }
}
