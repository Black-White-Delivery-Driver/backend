package com.sparta.blackwhitedeliverydriver.mock.user;

import com.sparta.blackwhitedeliverydriver.user.entity.UserRoleEnum;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MockSecurityContextFactory.class)
public @interface MockUser {
    String username() default "user1";
    String email() default "email1@email.com";
    String password() default "password";
    String phoneNumber() default "01011110000";
    UserRoleEnum role() default UserRoleEnum.CUSTOMER;
    boolean publicProfile() default true;

}
