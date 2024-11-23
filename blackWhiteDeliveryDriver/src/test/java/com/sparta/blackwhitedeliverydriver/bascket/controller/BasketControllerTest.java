package com.sparta.blackwhitedeliverydriver.bascket.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.blackwhitedeliverydriver.config.TestSecurityConfig;
import com.sparta.blackwhitedeliverydriver.bascket.dto.BasketAddRequestDto;
import com.sparta.blackwhitedeliverydriver.bascket.dto.BasketGetResponseDto;
import com.sparta.blackwhitedeliverydriver.bascket.dto.BasketResponseDto;
import com.sparta.blackwhitedeliverydriver.bascket.dto.BasketUpdateRequestDto;
import com.sparta.blackwhitedeliverydriver.model.user.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.mock.user.MockUser;
import com.sparta.blackwhitedeliverydriver.bascket.service.BasketService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BasketController.class)
@Import(TestSecurityConfig.class)
class BasketControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    BasketService basketService;

    @Autowired
    ObjectMapper mapper;

    private static final String BASE_URL = "/api/v1";


    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("장바구니 담기 성공")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void addProductToBasket_success() throws Exception {
        //given
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(UUID.randomUUID())
                .quantity(2)
                .build();
        BasketResponseDto response = BasketResponseDto.builder()
                .basketId(UUID.randomUUID())
                .build();

        //when
        when(basketService.addProductToBasket(any(), any())).thenReturn(response);

        //then
        String body = mapper.writeValueAsString(request);
        mvc.perform(post(BASE_URL + "/baskets")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.basketId").exists());
    }

    @Test
    @DisplayName("장바구니 담기 실패1 : 권한이 OWNER인 경우")
    @MockUser(role = UserRoleEnum.OWNER)
    void addProductToBasket_fail1() throws Exception {
        //given
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(UUID.randomUUID())
                .quantity(2)
                .build();
        BasketResponseDto response = BasketResponseDto.builder()
                .basketId(UUID.randomUUID())
                .build();

        //when
        when(basketService.addProductToBasket(any(), any())).thenReturn(response);

        //then
        String body = mapper.writeValueAsString(request);
        mvc.perform(post(BASE_URL + "/baskets")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("장바구니 담기 실패2 : 권한이 MANAGER인 경우")
    @MockUser(role = UserRoleEnum.MANAGER)
    void addProductToBasket_fail2() throws Exception {
        //given
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(UUID.randomUUID())
                .quantity(2)
                .build();
        BasketResponseDto response = BasketResponseDto.builder()
                .basketId(UUID.randomUUID())
                .build();

        //when
        when(basketService.addProductToBasket(any(), any())).thenReturn(response);

        //then
        String body = mapper.writeValueAsString(request);
        mvc.perform(post(BASE_URL + "/baskets")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("장바구니 담기 실패3 : 권한이 MASTER인 경우")
    @MockUser(role = UserRoleEnum.MASTER)
    void addProductToBasket_fail3() throws Exception {
        //given
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(UUID.randomUUID())
                .quantity(2)
                .build();
        BasketResponseDto response = BasketResponseDto.builder()
                .basketId(UUID.randomUUID())
                .build();

        //when
        when(basketService.addProductToBasket(any(), any())).thenReturn(response);

        //then
        String body = mapper.writeValueAsString(request);
        mvc.perform(post(BASE_URL + "/baskets")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("장바구니 빼기 성공")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void removeProductFromBasket_success() throws Exception {
        //given
        BasketResponseDto response = BasketResponseDto.builder()
                .basketId(UUID.randomUUID())
                .build();

        //when
        when(basketService.removeProductFromBasket(any(), any())).thenReturn(response);

        //then
        mvc.perform(delete(BASE_URL + "/baskets/{basketId}", response.getBasketId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basketId").exists());
    }

    @Test
    @DisplayName("장바구니 빼기 실패1 : 권한이 OWNER인 경우")
    @MockUser(role = UserRoleEnum.OWNER)
    void removeProductFromBasket_fail1() throws Exception {
        //given
        BasketResponseDto response = BasketResponseDto.builder()
                .basketId(UUID.randomUUID())
                .build();

        //when
        when(basketService.removeProductFromBasket(any(), any())).thenReturn(response);

        //then
        mvc.perform(delete(BASE_URL + "/baskets/{basketId}", response.getBasketId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("장바구니 빼기 실패2 : 권한이 MANAGER인 경우")
    @MockUser(role = UserRoleEnum.MANAGER)
    void removeProductFromBasket_fail2() throws Exception {
        //given
        BasketResponseDto response = BasketResponseDto.builder()
                .basketId(UUID.randomUUID())
                .build();

        //when
        when(basketService.removeProductFromBasket(any(), any())).thenReturn(response);

        //then
        mvc.perform(delete(BASE_URL + "/baskets/{basketId}", response.getBasketId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("장바구니 빼기 실패3 : 권한이 MASTER인 경우")
    @MockUser(role = UserRoleEnum.MASTER)
    void removeProductFromBasket_fail3() throws Exception {
        //given
        BasketResponseDto response = BasketResponseDto.builder()
                .basketId(UUID.randomUUID())
                .build();

        //when
        when(basketService.removeProductFromBasket(any(), any())).thenReturn(response);

        //then
        mvc.perform(delete(BASE_URL + "/baskets/{basketId}", response.getBasketId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("장바구니 조회 성공 : 권한이 CUSTOMER 경우")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    public void getBaskets_success1() throws Exception {
        // Given
        String username = "testUser";
        int page = 1;
        int size = 10;
        String sortBy = "createdDate";
        boolean isAsc = true;

        // Mocking the service response
        Page<BasketGetResponseDto> mockResponse = new PageImpl<>(List.of(BasketGetResponseDto.builder()
                .basketId(UUID.randomUUID()).build()),
                PageRequest.of(0, size), 1);
        when(basketService.getBaskets(username, 0, size, sortBy, isAsc)).thenReturn(mockResponse);

        // When & Then
        mvc.perform(get(BASE_URL + "/baskets")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sortBy", sortBy)
                        .param("isAsc", String.valueOf(isAsc))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("장바구니 조회 성공2 : 권한이 MANAGER 경우")
    @MockUser(role = UserRoleEnum.MANAGER)
    public void getBaskets_success2() throws Exception {
        // Given
        String username = "testUser";
        int page = 1;
        int size = 10;
        String sortBy = "createdDate";
        boolean isAsc = true;

        // Mocking the service response
        Page<BasketGetResponseDto> mockResponse = new PageImpl<>(List.of(BasketGetResponseDto.builder()
                .basketId(UUID.randomUUID()).build()),
                PageRequest.of(0, size), 1);
        when(basketService.getBaskets(username, 0, size, sortBy, isAsc)).thenReturn(mockResponse);

        // When & Then
        mvc.perform(get(BASE_URL + "/baskets")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sortBy", sortBy)
                        .param("isAsc", String.valueOf(isAsc))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("장바구니 조회 성공3 : 권한이 MASTER 경우")
    @MockUser(role = UserRoleEnum.MASTER)
    public void getBaskets_success3() throws Exception {
        // Given
        String username = "testUser";
        int page = 1;
        int size = 10;
        String sortBy = "createdDate";
        boolean isAsc = true;

        // Mocking the service response
        Page<BasketGetResponseDto> mockResponse = new PageImpl<>(List.of(BasketGetResponseDto.builder()
                .basketId(UUID.randomUUID()).build()),
                PageRequest.of(0, size), 1);
        when(basketService.getBaskets(username, 0, size, sortBy, isAsc)).thenReturn(mockResponse);

        // When & Then
        mvc.perform(get(BASE_URL + "/baskets")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sortBy", sortBy)
                        .param("isAsc", String.valueOf(isAsc))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("장바구니 조회 실패1 : 권한이 OWNER 경우")
    @MockUser(role = UserRoleEnum.OWNER)
    public void getBaskets_fail1() throws Exception {
        // Given
        String username = "testUser";
        int page = 1;
        int size = 10;
        String sortBy = "createdDate";
        boolean isAsc = true;

        // Mocking the service response
        Page<BasketGetResponseDto> mockResponse = new PageImpl<>(List.of(BasketGetResponseDto.builder()
                .basketId(UUID.randomUUID()).build()),
                PageRequest.of(0, size), 1);
        when(basketService.getBaskets(username, 0, size, sortBy, isAsc)).thenReturn(mockResponse);

        // When & Then
        mvc.perform(get(BASE_URL + "/baskets")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sortBy", sortBy)
                        .param("isAsc", String.valueOf(isAsc))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("장바구니 수정 성공")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void updateBasket_success() throws Exception {
        //given
        BasketUpdateRequestDto request = BasketUpdateRequestDto.builder()
                .basketId(UUID.randomUUID())
                .quantity(2)
                .build();
        BasketResponseDto response = BasketResponseDto.builder()
                .basketId(request.getBasketId())
                .build();
        //when
        when(basketService.updateBasket(any(), any())).thenReturn(response);

        //then
        String body = mapper.writeValueAsString(BasketUpdateRequestDto.builder()
                .basketId(request.getBasketId())
                .quantity(request.getQuantity())
                .build());

        mvc.perform(put(BASE_URL + "/baskets")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basketId").exists());
    }

    @Test
    @DisplayName("장바구니 수정 실패1 : 권한이 OWNER")
    @MockUser(role = UserRoleEnum.OWNER)
    void updateBasket_fail1() throws Exception {
        //given
        BasketUpdateRequestDto request = BasketUpdateRequestDto.builder()
                .basketId(UUID.randomUUID())
                .quantity(2)
                .build();
        BasketResponseDto response = BasketResponseDto.builder()
                .basketId(request.getBasketId())
                .build();
        //when
        when(basketService.updateBasket(any(), any())).thenReturn(response);

        //then
        String body = mapper.writeValueAsString(BasketUpdateRequestDto.builder()
                .basketId(request.getBasketId())
                .quantity(request.getQuantity())
                .build());

        mvc.perform(put(BASE_URL + "/baskets")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("장바구니 수정 실패2 : 권한이 MANAGER")
    @MockUser(role = UserRoleEnum.MANAGER)
    void updateBasket_fail2() throws Exception {
        //given
        BasketUpdateRequestDto request = BasketUpdateRequestDto.builder()
                .basketId(UUID.randomUUID())
                .quantity(2)
                .build();
        BasketResponseDto response = BasketResponseDto.builder()
                .basketId(request.getBasketId())
                .build();
        //when
        when(basketService.updateBasket(any(), any())).thenReturn(response);

        //then
        String body = mapper.writeValueAsString(BasketUpdateRequestDto.builder()
                .basketId(request.getBasketId())
                .quantity(request.getQuantity())
                .build());

        mvc.perform(put(BASE_URL + "/baskets")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("장바구니 수정 실패3 : 권한이 MASTER")
    @MockUser(role = UserRoleEnum.MANAGER)
    void updateBasket_fail3() throws Exception {
        //given
        BasketUpdateRequestDto request = BasketUpdateRequestDto.builder()
                .basketId(UUID.randomUUID())
                .quantity(2)
                .build();
        BasketResponseDto response = BasketResponseDto.builder()
                .basketId(request.getBasketId())
                .build();
        //when
        when(basketService.updateBasket(any(), any())).thenReturn(response);

        //then
        String body = mapper.writeValueAsString(BasketUpdateRequestDto.builder()
                .basketId(request.getBasketId())
                .quantity(request.getQuantity())
                .build());

        mvc.perform(put(BASE_URL + "/baskets")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}