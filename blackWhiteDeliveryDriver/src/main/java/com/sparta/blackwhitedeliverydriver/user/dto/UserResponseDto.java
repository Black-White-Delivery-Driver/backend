package com.sparta.blackwhitedeliverydriver.user.dto;

import com.sparta.blackwhitedeliverydriver.model.user.entity.User;
import com.sparta.blackwhitedeliverydriver.model.user.entity.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private String username;
    private String email;
    private String phoneNumber;
    private boolean publicProfile;
    private UserRoleEnum role;
    private String imgUrl;

    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .publicProfile(user.isPublicProfile())
                .role(user.getRole())
                .imgUrl(user.getImageUrl())
                .build();
    }
}
