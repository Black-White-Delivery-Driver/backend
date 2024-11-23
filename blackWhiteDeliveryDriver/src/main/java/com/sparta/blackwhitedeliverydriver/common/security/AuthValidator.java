package com.sparta.blackwhitedeliverydriver.common.security;

import com.sparta.blackwhitedeliverydriver.user.entity.User;
import com.sparta.blackwhitedeliverydriver.user.exception.ExceptionMessage;
import com.sparta.blackwhitedeliverydriver.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthValidator {
    private final UserRepository userRepository;

    @Autowired
    public AuthValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateHttpMethod(HttpServletRequest request) {
        if (!request.getMethod().equals(HttpMethod.POST.name())) {
            throw new IllegalArgumentException(ExceptionMessage.NOT_ALLOWED_METHOD.getMessage());
        }
    }

    public void validateNotAlreadyLoggedIn() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            throw new IllegalStateException(ExceptionMessage.ALREADY_LOGGED_IN.getMessage());
        }
    }

    public void validateUser(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        if (user.getDeletedBy() != null && user.getDeletedDate() != null) {
            throw new IllegalStateException(ExceptionMessage.USER_DELETED.getMessage());
        }
    }
}
