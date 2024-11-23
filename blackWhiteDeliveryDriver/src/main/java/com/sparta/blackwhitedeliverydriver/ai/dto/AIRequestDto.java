package com.sparta.blackwhitedeliverydriver.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AIRequestDto {
    @NotBlank
    @Size(min = 2, max = 50)
    private String prompt;
}
