package com.sparta.blackwhitedeliverydriver.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.blackwhitedeliverydriver.config.TestSecurityConfig;
import com.sparta.blackwhitedeliverydriver.user.dto.SignupRequestDto;
import com.sparta.blackwhitedeliverydriver.user.dto.UpdateUserRequestDto;
import com.sparta.blackwhitedeliverydriver.user.dto.UserResponseDto;
import com.sparta.blackwhitedeliverydriver.user.dto.UsernameResponseDto;
import com.sparta.blackwhitedeliverydriver.model.user.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.user.exception.ExceptionMessage;
import com.sparta.blackwhitedeliverydriver.mock.user.MockUser;
import com.sparta.blackwhitedeliverydriver.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
public class UserControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper mapper;

    private static final String BASE_URL = "/api/v1";

    @Test
    @DisplayName("일반 사용자 회원가입")
    void signup() throws Exception {
        // given
        SignupRequestDto requestDto = SignupRequestDto.builder()
                .username("test")
                .password("password")
                .email("test@email.com")
                .phoneNumber("01012345678")
                .role(UserRoleEnum.CUSTOMER)
                .imgUrl(null)
                .build();

        // when
        when(userService.signup(any(SignupRequestDto.class), eq(null)))
                .thenReturn(new UsernameResponseDto("test"));

        // then
        mvc.perform(post(BASE_URL + "/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("test"));
    }

    @Test
    @DisplayName("일반 사용자 회원가입 실패 - 허용되지 않은 role 가입 시도")
    void signupSetRoleManager() throws Exception {
        // given
        SignupRequestDto requestDto = SignupRequestDto.builder()
                .username("test")
                .password("password")
                .email("test@email.com")
                .phoneNumber("01012345678")
                .role(UserRoleEnum.MANAGER)
                .imgUrl(null)
                .build();

        // when
        when(userService.signup(argThat(dto -> dto.getRole() == UserRoleEnum.MANAGER), eq(null)))
                .thenThrow(new IllegalArgumentException(ExceptionMessage.NOT_ALLOEWD_ROLE.getMessage()));

        // then
        mvc.perform(post(BASE_URL + "/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(ExceptionMessage.NOT_ALLOEWD_ROLE.getMessage()));
    }

    @Test
    @DisplayName("관리자 회원가입")
    @MockUser(role = UserRoleEnum.MASTER)
    void signupManager() throws Exception {
        // given
        SignupRequestDto requestDto = SignupRequestDto.builder()
                .username("test")
                .password("password")
                .email("test@email.com")
                .phoneNumber("01012345678")
                .role(UserRoleEnum.MANAGER)
                .imgUrl(null)
                .build();

        // when
        when(userService.signup(any(SignupRequestDto.class), eq(UserRoleEnum.MASTER)))
                .thenReturn(new UsernameResponseDto("test"));

        // then
        mvc.perform(post(BASE_URL + "/users/signup/master")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("test"));
    }

    @Test
    @DisplayName("관리자 회원가입 실패 - 권한 부족")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void signupManagerByNotAdmin() throws Exception {
        // given
        SignupRequestDto requestDto = SignupRequestDto.builder()
                .username("test")
                .password("password")
                .email("test@email.com")
                .phoneNumber("01012345678")
                .role(UserRoleEnum.MANAGER)
                .imgUrl(null)
                .build();

        // when - then
        mvc.perform(post(BASE_URL + "/users/signup/master")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorMessage").value(ExceptionMessage.NOT_ALLOWED_API.getMessage()));
    }

    @Test
    @DisplayName("사용자 정보 조회")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void getUserInfo() throws Exception {
        // given
        UserResponseDto responseDto = UserResponseDto.builder()
                .username("user")
                .email("user@email.com")
                .phoneNumber("01012345678")
                .role(UserRoleEnum.CUSTOMER)
                .imgUrl(null)
                .build();

        when(userService.getUserInfo(eq("user"), any(String.class)))
                .thenReturn(responseDto);

        // when - then
        mvc.perform(get(BASE_URL + "/users/")
                        .param("username", "user"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.email").value("user@email.com"))
                .andExpect(jsonPath("$.phoneNumber").value("01012345678"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    @DisplayName("사용자 정보 수정")
    @MockUser(username = "test", role = UserRoleEnum.CUSTOMER)
    void updateUser() throws Exception {
        // given
        UpdateUserRequestDto requestDto = UpdateUserRequestDto.builder()
                .password("password")
                .email("test@email.com")
                .phoneNumber("01012345678")
//                .role(UserRoleEnum.MANAGER)
                .imgUrl(null)
                .build();

        // when
        when(userService.updateUser(any(UpdateUserRequestDto.class), eq("test")))
                .thenReturn(new UsernameResponseDto("test"));

        // then
        mvc.perform(put(BASE_URL + "/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test"));
    }

    @Test
    @DisplayName("관리자의 사용자 정보 수정")
    @MockUser(role = UserRoleEnum.MANAGER)
    void updateUserByManager() throws Exception {
        // given
        UpdateUserRequestDto requestDto = UpdateUserRequestDto.builder()
                .password("password")
                .email("test@email.com")
                .phoneNumber("01012345678")
//                .role(UserRoleEnum.MANAGER)
                .imgUrl(null)
                .build();

        // when
        when(userService.updateUser(any(UpdateUserRequestDto.class), any(String.class)))
                .thenReturn(new UsernameResponseDto("test"));

        // then
        mvc.perform(put(BASE_URL + "/users/{username}", "test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test"));
    }

    @Test
    @DisplayName("관리자의 사용자 정보 수정 실패 - 권한 부족")
    @MockUser(username = "test", role = UserRoleEnum.CUSTOMER)
    void updateUserByCustomer() throws Exception {
        // given
        UpdateUserRequestDto requestDto = UpdateUserRequestDto.builder()
                .password("password")
                .email("test@email.com")
                .phoneNumber("01012345678")
//                .role(UserRoleEnum.MANAGER)
                .imgUrl(null)
                .build();

        // when - then
        mvc.perform(put(BASE_URL + "/users/{username}", "test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorMessage").value(ExceptionMessage.NOT_ALLOWED_API.getMessage()));
    }

    @Test
    @DisplayName("사용자 정보 삭제")
    @MockUser(username = "test", role = UserRoleEnum.CUSTOMER)
    void deleteUser() throws Exception {
        // given
        UsernameResponseDto responseDto = new UsernameResponseDto("test");

        // when
        when(userService.deleteUser(eq("test")))
                .thenReturn(new UsernameResponseDto("test"));

        // then
        mvc.perform(delete(BASE_URL + "/users/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(responseDto.getUsername()));
    }

    @Test
    @DisplayName("관리자의 사용자 정보 삭제")
    @MockUser(role = UserRoleEnum.MANAGER)
    void deleteUserByManager() throws Exception {
        // given
        UsernameResponseDto responseDto = new UsernameResponseDto("test");

        // when
        when(userService.deleteUser(eq("test")))
                .thenReturn(new UsernameResponseDto("test"));

        // then
        mvc.perform(delete(BASE_URL + "/users/{username}", responseDto.getUsername()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(responseDto.getUsername()));
    }

    @Test
    @DisplayName("관리자의 사용자 정보 삭제 실패 - 권한 부족")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void deleteUserByCustomer() throws Exception {
        // given
        UsernameResponseDto responseDto = new UsernameResponseDto("test");

        // when - then
        mvc.perform(delete(BASE_URL + "/users/{username}", responseDto.getUsername()))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorMessage").value(ExceptionMessage.NOT_ALLOWED_API.getMessage()));
    }
}
