package com.sparta.blackwhitedeliverydriver.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.blackwhitedeliverydriver.config.TestSecurityConfig;
import com.sparta.blackwhitedeliverydriver.dto.OrderAddRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.OrderGetDetailResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.OrderGetResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.OrderResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.OrderUpdateRequestDto;
import com.sparta.blackwhitedeliverydriver.entity.OrderStatusEnum;
import com.sparta.blackwhitedeliverydriver.entity.OrderTypeEnum;
import com.sparta.blackwhitedeliverydriver.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.mock.user.MockUser;
import com.sparta.blackwhitedeliverydriver.service.OrderService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
@Import(TestSecurityConfig.class)
class OrderControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    OrderService orderService;
    @Autowired
    ObjectMapper mapper;

    private static final String BASE_URL = "/api/v1";

    @Test
    @DisplayName("주문 생성하기 성공")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void createOrder_success() throws Exception {
        //given
        OrderResponseDto response = new OrderResponseDto(UUID.randomUUID());
        OrderAddRequestDto request = new OrderAddRequestDto(OrderTypeEnum.ONLINE);

        //when
        when(orderService.createOrder(any(), any())).thenReturn(response);

        //then
        String body = mapper.writeValueAsString(request);
        mvc.perform(post(BASE_URL + "/orders")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").exists());

    }

    @Test
    @DisplayName("주문 생성하기 실패1 : OWNER 권한일 때")
    @MockUser(role = UserRoleEnum.OWNER)
    void createOrder_fail1() throws Exception {
        //given
        OrderResponseDto response = new OrderResponseDto(UUID.randomUUID());
        OrderAddRequestDto request = new OrderAddRequestDto(OrderTypeEnum.ONLINE);

        //when
        when(orderService.createOrder(any(), any())).thenReturn(response);

        //then
        String body = mapper.writeValueAsString(request);
        mvc.perform(post(BASE_URL + "/orders")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("주문 생성하기 실패 : MANAGER 권한일 때")
    @MockUser(role = UserRoleEnum.MANAGER)
    void createOrder_fail2() throws Exception {
        //given
        OrderResponseDto response = new OrderResponseDto(UUID.randomUUID());
        OrderAddRequestDto request = new OrderAddRequestDto(OrderTypeEnum.ONLINE);

        //when
        when(orderService.createOrder(any(), any())).thenReturn(response);

        //then
        String body = mapper.writeValueAsString(request);
        mvc.perform(post(BASE_URL + "/orders")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("주문 생성하기 실패3 : MASTER 권한일 때")
    @MockUser(role = UserRoleEnum.MASTER)
    void createOrder_fail3() throws Exception {
        //given
        OrderResponseDto response = new OrderResponseDto(UUID.randomUUID());
        OrderAddRequestDto request = new OrderAddRequestDto(OrderTypeEnum.ONLINE);

        //when
        when(orderService.createOrder(any(), any())).thenReturn(response);

        //then
        String body = mapper.writeValueAsString(request);
        mvc.perform(post(BASE_URL + "/orders")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("주문 상세 조회하기 성공1 : MASTER 일 때")
    @MockUser(role = UserRoleEnum.MASTER)
    void getOrderDetail_success1() throws Exception {
        //given
        OrderGetDetailResponseDto response = OrderGetDetailResponseDto.builder()
                .orderId(UUID.randomUUID())
                .username("user")
                .finalPay(10000)
                .build();

        //when
        when(orderService.getOrderDetail(any(), any())).thenReturn(response);

        //then
        mvc.perform(get(BASE_URL + "/orders/{orderId}", response.getOrderId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.username").exists());
    }

    @Test
    @DisplayName("주문 상세 조회하기 성공2 : MANAGER 일 때")
    @MockUser(role = UserRoleEnum.MANAGER)
    void getOrderDetail_success2() throws Exception {
        //given
        OrderGetDetailResponseDto response = OrderGetDetailResponseDto.builder()
                .orderId(UUID.randomUUID())
                .username("user")
                .finalPay(10000)
                .build();

        //when
        when(orderService.getOrderDetail(any(), any())).thenReturn(response);

        //then
        mvc.perform(get(BASE_URL + "/orders/{orderId}", response.getOrderId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.username").exists());
    }

    @Test
    @DisplayName("주문 상세 조회하기 성공3 : CUSTOMER 일 때")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void getOrderDetail_success3() throws Exception {
        //given
        OrderGetDetailResponseDto response = OrderGetDetailResponseDto.builder()
                .orderId(UUID.randomUUID())
                .username("user")
                .finalPay(10000)
                .build();

        //when
        when(orderService.getOrderDetail(any(), any())).thenReturn(response);

        //then
        mvc.perform(get(BASE_URL + "/orders/{orderId}", response.getOrderId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.username").exists());
    }

    @Test
    @DisplayName("주문 상세 조회하기 실패 : OWNER 일 때")
    @MockUser(role = UserRoleEnum.OWNER)
    void getOrderDetail_fail1() throws Exception {
        //given
        OrderGetDetailResponseDto response = OrderGetDetailResponseDto.builder()
                .orderId(UUID.randomUUID())
                .username("user")
                .finalPay(10000)
                .build();

        //when
        when(orderService.getOrderDetail(any(), any())).thenReturn(response);

        //then
        mvc.perform(get(BASE_URL + "/orders/{orderId}", response.getOrderId()))
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("주문 목록 조회하기 성공1 : CUSTOMER 일 때")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void getOrders_success1() throws Exception {
        // Given
        OrderGetResponseDto response = OrderGetResponseDto.builder()
                .orderId(UUID.randomUUID())
                .username("user")
                .finalPay(10000)
                .build();

        Page<OrderGetResponseDto> responsePage = new PageImpl<>(
                List.of(response), // 페이지에 포함된 데이터
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdAt")), // 페이징 정보
                1 // 총 데이터 개수
        );

        // When
        when(orderService.getOrders(anyString(), anyInt(), anyInt(), anyString(), anyBoolean()))
                .thenReturn(responsePage);

        // Then
        mvc.perform(get(BASE_URL + "/orders")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("isAsc", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("주문 목록 조회하기 성공2 : MANAGER 일 때")
    @MockUser(role = UserRoleEnum.MANAGER)
    void getOrders_success2() throws Exception {
        // Given
        OrderGetResponseDto response = OrderGetResponseDto.builder()
                .orderId(UUID.randomUUID())
                .username("user")
                .finalPay(10000)
                .build();

        Page<OrderGetResponseDto> responsePage = new PageImpl<>(
                List.of(response), // 페이지에 포함된 데이터
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdAt")), // 페이징 정보
                1 // 총 데이터 개수
        );

        // When
        when(orderService.getOrders(anyString(), anyInt(), anyInt(), anyString(), anyBoolean()))
                .thenReturn(responsePage);

        // Then
        mvc.perform(get(BASE_URL + "/orders")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("isAsc", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("주문 목록 조회하기 성공3 : MASTER 일 때")
    @MockUser(role = UserRoleEnum.MASTER)
    void getOrders_success3() throws Exception {
        // Given
        OrderGetResponseDto response = OrderGetResponseDto.builder()
                .orderId(UUID.randomUUID())
                .username("user")
                .finalPay(10000)
                .build();

        Page<OrderGetResponseDto> responsePage = new PageImpl<>(
                List.of(response), // 페이지에 포함된 데이터
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdAt")), // 페이징 정보
                1 // 총 데이터 개수
        );

        // When
        when(orderService.getOrders(anyString(), anyInt(), anyInt(), anyString(), anyBoolean()))
                .thenReturn(responsePage);

        // Then
        mvc.perform(get(BASE_URL + "/orders")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("isAsc", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("주문 목록 조회하기 실패 : OWNER 일 때")
    @MockUser(role = UserRoleEnum.OWNER)
    void getOrders_fail() throws Exception {
        // Given
        OrderGetResponseDto response = OrderGetResponseDto.builder()
                .orderId(UUID.randomUUID())
                .username("user")
                .finalPay(10000)
                .build();

        Page<OrderGetResponseDto> responsePage = new PageImpl<>(
                List.of(response), // 페이지에 포함된 데이터
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdAt")), // 페이징 정보
                1 // 총 데이터 개수
        );

        // When
        when(orderService.getOrders(anyString(), anyInt(), anyInt(), anyString(), anyBoolean()))
                .thenReturn(responsePage);

        // Then
        mvc.perform(get(BASE_URL + "/orders")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("isAsc", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("주문 상태 수정하기 성공1 : OWNER 역할")
    @MockUser(role = UserRoleEnum.OWNER)
    void updateOrderStatus_success1() throws Exception {
        // Given
        UUID orderId = UUID.randomUUID();
        OrderUpdateRequestDto request = new OrderUpdateRequestDto(orderId, OrderStatusEnum.ACCEPTED);
        OrderResponseDto response = new OrderResponseDto(orderId);

        when(orderService.updateOrderStatus(any(), any())).thenReturn(response);

        String body = mapper.writeValueAsString(request);

        // When & Then
        mvc.perform(put(BASE_URL + "/orders")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()));
    }

    @Test
    @DisplayName("주문 상태 수정하기 성공2 : MASTER 역할")
    @MockUser(role = UserRoleEnum.MASTER)
    void updateOrderStatus_success2() throws Exception {
        // Given
        UUID orderId = UUID.randomUUID();
        OrderUpdateRequestDto request = new OrderUpdateRequestDto(orderId, OrderStatusEnum.ACCEPTED);
        OrderResponseDto response = new OrderResponseDto(orderId);

        when(orderService.updateOrderStatus(any(), any())).thenReturn(response);

        String body = mapper.writeValueAsString(request);

        // When & Then
        mvc.perform(put(BASE_URL + "/orders")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()));
    }

    @Test
    @DisplayName("주문 상태 수정하기 성공3 : MANAGER 역할")
    @MockUser(role = UserRoleEnum.MANAGER)
    void updateOrderStatus_success3() throws Exception {
        // Given
        UUID orderId = UUID.randomUUID();
        OrderUpdateRequestDto request = new OrderUpdateRequestDto(orderId, OrderStatusEnum.ACCEPTED);
        OrderResponseDto response = new OrderResponseDto(orderId);

        when(orderService.updateOrderStatus(any(), any())).thenReturn(response);

        String body = mapper.writeValueAsString(request);

        // When & Then
        mvc.perform(put(BASE_URL + "/orders")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()));
    }

    @Test
    @DisplayName("주문 상태 수정하기 실패 : CUSTOMER 역할")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void updateOrderStatus_fail() throws Exception {
        // Given
        UUID orderId = UUID.randomUUID();
        OrderUpdateRequestDto request = new OrderUpdateRequestDto(orderId, OrderStatusEnum.ACCEPTED);

        String body = mapper.writeValueAsString(request);

        // When & Then
        mvc.perform(put(BASE_URL + "/orders")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("주문 취소하기 성공1 : CUSTOMER 일 때")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void deleteOrder_success1() throws Exception {
        //given
        OrderResponseDto response = new OrderResponseDto(UUID.randomUUID());

        //when
        when(orderService.deleteOrder(any(), any())).thenReturn(response);

        //then
        mvc.perform(delete(BASE_URL + "/orders/{orderId}", response.getOrderId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").exists());
    }

    @Test
    @DisplayName("주문 취소하기 성공2 : MASTER 일 때")
    @MockUser(role = UserRoleEnum.MASTER)
    void deleteOrder_success2() throws Exception {
        //given
        OrderResponseDto response = new OrderResponseDto(UUID.randomUUID());

        //when
        when(orderService.deleteOrder(any(), any())).thenReturn(response);

        //then
        mvc.perform(delete(BASE_URL + "/orders/{orderId}", response.getOrderId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").exists());
    }

    @Test
    @DisplayName("주문 취소하기 성공3 : MANAGER 일 때")
    @MockUser(role = UserRoleEnum.MANAGER)
    void deleteOrder_success3() throws Exception {
        //given
        OrderResponseDto response = new OrderResponseDto(UUID.randomUUID());

        //when
        when(orderService.deleteOrder(any(), any())).thenReturn(response);

        //then
        mvc.perform(delete(BASE_URL + "/orders/{orderId}", response.getOrderId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").exists());
    }

    @Test
    @DisplayName("주문 취소하기 실패 : OWNER 권한")
    @MockUser(role = UserRoleEnum.OWNER)
    void deleteOrder_fail() throws Exception {
        //given
        UUID orderId = UUID.randomUUID();
        OrderResponseDto response = new OrderResponseDto(orderId);

        //when
        when(orderService.deleteOrder(any(), any())).thenReturn(response);

        //then
        mvc.perform(delete(BASE_URL + "/orders/{orderId}", orderId.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("주문 검색 성공1 : MASTER 역할")
    @MockUser(role = UserRoleEnum.MASTER)
    void searchOrders_success1() throws Exception {
        // Given
        String storeName = "Test Store";
        int page = 1;
        int size = 10;
        String sortBy = "createdAt";
        boolean isAsc = true;

        OrderGetResponseDto response = OrderGetResponseDto.builder()
                .orderId(UUID.randomUUID())
                .username("user")
                .build();

        Page<OrderGetResponseDto> mockPage = new PageImpl<>(
                List.of(response),
                PageRequest.of(0, size, Sort.by(Sort.Direction.ASC, sortBy)),
                2
        );

        when(orderService.searchOrdersByStoreName(eq(storeName), eq(0), eq(size), eq(sortBy), eq(isAsc)))
                .thenReturn(mockPage);

        // When & Then
        mvc.perform(get(BASE_URL + "/orders/search")
                        .param("storeName", storeName)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sortBy", sortBy)
                        .param("isAsc", String.valueOf(isAsc))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("주문 검색 성공2 : MANAGER 역할")
    @MockUser(role = UserRoleEnum.MANAGER)
    void searchOrders_success2() throws Exception {
        // Given
        String storeName = "Test Store";
        int page = 1;
        int size = 10;
        String sortBy = "createdAt";
        boolean isAsc = true;

        OrderGetResponseDto response = OrderGetResponseDto.builder()
                .orderId(UUID.randomUUID())
                .username("user")
                .build();

        Page<OrderGetResponseDto> mockPage = new PageImpl<>(
                List.of(response),
                PageRequest.of(0, size, Sort.by(Sort.Direction.ASC, sortBy)),
                2
        );

        when(orderService.searchOrdersByStoreName(eq(storeName), eq(0), eq(size), eq(sortBy), eq(isAsc)))
                .thenReturn(mockPage);

        // When & Then
        mvc.perform(get(BASE_URL + "/orders/search")
                        .param("storeName", storeName)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sortBy", sortBy)
                        .param("isAsc", String.valueOf(isAsc))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("주문 검색 실패 - CUSTOMER 역할")
    @MockUser(role = UserRoleEnum.OWNER)
    void searchOrders_fail1() throws Exception {
        // When & Then
        mvc.perform(get(BASE_URL + "/orders/search")
                        .param("storeName", "Unauthorized Store")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("isAsc", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("주문 검색 실패 - CUSTOMER 역할")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void searchOrders_fail2() throws Exception {
        // When & Then
        mvc.perform(get(BASE_URL + "/orders/search")
                        .param("storeName", "Unauthorized Store")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("isAsc", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}