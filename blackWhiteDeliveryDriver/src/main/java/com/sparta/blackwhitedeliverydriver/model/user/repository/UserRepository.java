package com.sparta.blackwhitedeliverydriver.model.user.repository;

import com.sparta.blackwhitedeliverydriver.model.user.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Page<User> findByUsernameContaining(String keyword, Pageable pageable);

    Page<User> findByUsernameContainingAndDeletedByIsNullAndDeletedDateIsNullAndPublicProfileIsTrue(String keyword, Pageable pageable);

    Optional<User> findByUsernameAndDeletedDateIsNullAndDeletedByIsNullAndPublicProfileTrue(String username);
}