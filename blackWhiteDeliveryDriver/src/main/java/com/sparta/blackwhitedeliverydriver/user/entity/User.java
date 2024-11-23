package com.sparta.blackwhitedeliverydriver.user.entity;

import com.sparta.blackwhitedeliverydriver.address.entity.Address;
import com.sparta.blackwhitedeliverydriver.common.entity.BaseEntity;
import com.sparta.blackwhitedeliverydriver.user.dto.SignupRequestDto;
import com.sparta.blackwhitedeliverydriver.user.dto.UpdateUserRequestDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_user")
public class User extends BaseEntity {
    @Id
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    private boolean publicProfile;

    private String imageUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address currentAddress;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    // 정적 팩토리 메서드
    public static User from(SignupRequestDto requestDto, PasswordEncoder passwordEncoder) {
        return User.builder()
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .phoneNumber(requestDto.getPhoneNumber())
                .role(requestDto.getRole())
                .publicProfile(true)
                .imageUrl(requestDto.getImgUrl())
                .build();
    }

    // 필드 값 업데이트를 위한 메서드
    public void update(UpdateUserRequestDto requestDto, PasswordEncoder passwordEncoder) {
        this.email = requestDto.getEmail();
        this.password = passwordEncoder.encode(requestDto.getPassword());
        this.phoneNumber = requestDto.getPhoneNumber();
        this.publicProfile = requestDto.isPublicProfile();
        this.imageUrl = requestDto.getImgUrl();
    }

    public void updateCurrentAddress(Address address) {
        this.currentAddress = address;
    }
}
