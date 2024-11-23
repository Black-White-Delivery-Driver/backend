package com.sparta.blackwhitedeliverydriver.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewRequestDto {
    @NotBlank
    private String contents;
    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;
}
