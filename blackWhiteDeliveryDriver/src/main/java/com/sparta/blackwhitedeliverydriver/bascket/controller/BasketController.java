package com.sparta.blackwhitedeliverydriver.bascket.controller;

import com.sparta.blackwhitedeliverydriver.bascket.dto.BasketAddRequestDto;
import com.sparta.blackwhitedeliverydriver.bascket.dto.BasketGetResponseDto;
import com.sparta.blackwhitedeliverydriver.bascket.dto.BasketResponseDto;
import com.sparta.blackwhitedeliverydriver.bascket.dto.BasketUpdateRequestDto;
import com.sparta.blackwhitedeliverydriver.common.security.UserDetailsImpl;
import com.sparta.blackwhitedeliverydriver.bascket.service.BasketService;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/baskets")
public class BasketController {

    private final BasketService basketService;

    @Secured({"ROLE_CUSTOMER"})
    @PostMapping
    public ResponseEntity<BasketResponseDto> addProductToBasket(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                @RequestBody @Valid BasketAddRequestDto request) {
        //장바구니 추가
        BasketResponseDto response = basketService.addProductToBasket(userDetails.getUsername(), request);

        //201 응답
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Secured({"ROLE_CUSTOMER"})
    @DeleteMapping("/{basketId}")
    public ResponseEntity<BasketResponseDto> removeProductFromBasket(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID basketId) {
        BasketResponseDto response = basketService.removeProductFromBasket(userDetails.getUsername(), basketId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Secured({"ROLE_CUSTOMER", "ROLE_MANAGER", "ROLE_MASTER"})
    @GetMapping
    public ResponseEntity<Page<BasketGetResponseDto>> getBaskets(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "true") boolean isAsc) {

        //장바구니 리스트 조회
        Page<BasketGetResponseDto> responseDtoList = basketService.getBaskets(userDetails.getUsername(), page - 1, size,
                sortBy, isAsc);

        //200 응답
        return ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
    }

    @Secured({"ROLE_CUSTOMER"})
    @PutMapping
    public ResponseEntity<BasketResponseDto> updateBasket(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @RequestBody @Valid BasketUpdateRequestDto request) {
        //장바구니 수정
        BasketResponseDto response = basketService.updateBasket(userDetails.getUsername(), request);
        //200 응답
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Secured({"ROLE_CUSTOMER"})
    @GetMapping("/search")//테스트 완료
    public ResponseEntity<Page<BasketGetResponseDto>> searchBaskets(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("productName") String productName,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "true") boolean isAsc) {

        Page<BasketGetResponseDto> baskets = basketService.searchBasketsByProductName(userDetails.getUsername(),
                productName, page - 1, size, sortBy, isAsc);
        return ResponseEntity.ok(baskets);
    }
}
