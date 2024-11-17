package com.sparta.blackwhitedeliverydriver.dto;

import com.sparta.blackwhitedeliverydriver.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDto {
    String name;

    public static CategoryResponseDto from(Category category) {
        return CategoryResponseDto.builder()
                .name(category.getName())
                .build();
    }
}