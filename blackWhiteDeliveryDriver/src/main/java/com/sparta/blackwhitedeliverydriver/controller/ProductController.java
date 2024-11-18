package com.sparta.blackwhitedeliverydriver.controller;

import com.sparta.blackwhitedeliverydriver.dto.CreateProductRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.ProductIdResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.ProductRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.ProductResponseDto;
import com.sparta.blackwhitedeliverydriver.security.UserDetailsImpl;
import com.sparta.blackwhitedeliverydriver.service.ProductService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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
@RequestMapping("api/v1/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/")
    public ResponseEntity<?> getProducts(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "true") boolean isAsc,
            Sort sort,
            @RequestParam UUID storeId
    ) {
        // 해당 가게의 모든 음식 조회
        Page<ProductResponseDto> productResponseDtoPage = productService.getProducts(
                storeId, page - 1, size, sortBy, isAsc
        );
        return ResponseEntity.status(HttpStatus.OK).body(productResponseDtoPage);
    }

    @Secured({"ROLE_OWNER", "ROLE_MANAGER", "ROLE_MASTER"})
    @PostMapping("/")
    public ResponseEntity<?> createProduct(@RequestBody CreateProductRequestDto requestDto,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ProductIdResponseDto productIdResponseDto = productService.createProduct(requestDto, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(productIdResponseDto);
    }

    @Secured({"ROLE_OWNER", "ROLE_MANAGER", "ROLE_MASTER"})
    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable UUID productId, @RequestBody ProductRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        // 음식 수정
        ProductIdResponseDto productIdResponseDto = productService.updateProduct(productId, requestDto, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(productIdResponseDto);
    }

    @Secured({"ROLE_OWNER", "ROLE_MANAGER", "ROLE_MASTER"})
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable UUID productId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        // 음식 삭제
        ProductIdResponseDto productIdResponseDto = productService.deleteProduct(productId, userDetails);

        return ResponseEntity.status(HttpStatus.OK).body(productIdResponseDto);
    }
}
