package com.sparta.blackwhitedeliverydriver.user.dto;

import com.sparta.blackwhitedeliverydriver.model.user.entity.UserRoleEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupRequestDto {
    @NotBlank
    @Size(min = 4, max = 10)
    @Pattern(regexp = "^[a-z0-9]+$")
    private String username;
    @NotBlank
    @Size(min = 8, max = 15)
    @Pattern(regexp = "^[a-zA-Z\\d!@#$%^&*()_+\\-=]*$")
    private String password;
    @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 11, max = 11)
    @Pattern(regexp = "^[0-9]{11}$")
    private String phoneNumber;
    @NotNull
    private UserRoleEnum role;
    @Null
    private String imgUrl;
}
