package com.sparta.blackwhitedeliverydriver.controller;

import com.sparta.blackwhitedeliverydriver.dto.OrderAddRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.OrderGetDetailResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.OrderGetResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.OrderResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.OrderUpdateRequestDto;
import com.sparta.blackwhitedeliverydriver.security.UserDetailsImpl;
import com.sparta.blackwhitedeliverydriver.service.OrderService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Secured({"ROLE_CUSTOMER"})
    @PostMapping//테스트 완료
    public ResponseEntity<OrderResponseDto> createOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                        @RequestBody @Valid OrderAddRequestDto request) {
        //주문서 생성
        OrderResponseDto response = orderService.createOrder(userDetails.getUsername(), request);
        //201 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Secured({"ROLE_CUSTOMER", "ROLE_MASTER", "ROLE_MANAGER"})
    @GetMapping("/{orderId}")//테스트 완료
    public ResponseEntity<OrderGetDetailResponseDto> getOrderDetail(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID orderId) {
        //주문서 상세 조회
        OrderGetDetailResponseDto response = orderService.getOrderDetail(userDetails.getUsername(), orderId);
        //200 반환
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Secured({"ROLE_CUSTOMER", "ROLE_MASTER", "ROLE_MANAGER"})
    @GetMapping//테스트 완료
    public ResponseEntity<Page<OrderGetResponseDto>> getOrders(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "true") boolean isAsc) {
        //주문 목록 조회
        Page<OrderGetResponseDto> responseList = orderService.getOrders(userDetails.getUsername(), page - 1, size,
                sortBy, isAsc);
        //200 반환
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @Secured({"ROLE_OWNER", "ROLE_MASTER", "ROLE_MANAGER"})
    @GetMapping("/stores/{storeId}")//테스트 완료
    public ResponseEntity<Page<OrderGetResponseDto>> getOrdersByStore(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "true") boolean isAsc,
            @PathVariable UUID storeId) {
        //주문 목록 조회
        Page<OrderGetResponseDto> responseList = orderService.getOrdersByStore(userDetails.getUsername(), page - 1,
                size,
                sortBy, isAsc, storeId);
        //200 반환
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @Secured({"ROLE_OWNER", "ROLE_MASTER", "ROLE_MANAGER"})
    @PutMapping
    public ResponseEntity<OrderResponseDto> updateOrderStatus(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              @RequestBody @Valid OrderUpdateRequestDto request) {
        //주문 상태 변경
        OrderResponseDto response = orderService.updateOrderStatus(userDetails.getUsername(), request);
        //200 반환
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Secured({"ROLE_CUSTOMER", "ROLE_MASTER", "ROLE_MANAGER"})
    @DeleteMapping("/{orderId}")//테스트 완료
    public ResponseEntity<OrderResponseDto> deleteOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                        @PathVariable UUID orderId) {
        //주문 취소
        OrderResponseDto response = orderService.deleteOrder(userDetails.getUsername(), orderId);
        //200 반환
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Secured({"ROLE_MASTER", "ROLE_MANAGER"})
    @GetMapping("/search")//테스트 완료
    public ResponseEntity<Page<OrderGetResponseDto>> searchOrders(
            @RequestParam("storeName") String storeName,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "true") boolean isAsc) {
        // 서비스 호출
        Page<OrderGetResponseDto> responseList = orderService.searchOrdersByStoreName(
                storeName, page - 1, size, sortBy, isAsc);

        return ResponseEntity.ok(responseList);
    }
}