package com.sparta.blackwhitedeliverydriver.mock.user;

import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.jwt.JwtUtil;
import com.sparta.blackwhitedeliverydriver.security.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class MockSecurityContextFactory implements WithSecurityContextFactory<MockUser> {
    @Override
    public SecurityContext createSecurityContext(MockUser annotation) {
        User user = User.builder()
                .username(annotation.username())
                .email(annotation.email())
                .password(annotation.password())
                .phoneNumber(annotation.phoneNumber())
                .role(annotation.role())
                .build();

        UserDetails userDetails = new UserDetailsImpl(user);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(userDetails);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        return securityContext;
    }
}
