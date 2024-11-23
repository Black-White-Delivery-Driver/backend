package com.sparta.blackwhitedeliverydriver.review.dto;

import com.sparta.blackwhitedeliverydriver.model.review.entity.Review;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDto {
    private UUID id;
    private String contents;
    private Integer rating;

    public static ReviewResponseDto from(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .contents(review.getContents())
                .rating(review.getRating())
                .build();
    }
}
