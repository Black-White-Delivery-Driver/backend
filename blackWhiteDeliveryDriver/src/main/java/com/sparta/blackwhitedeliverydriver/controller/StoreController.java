package com.sparta.blackwhitedeliverydriver.controller;

import com.sparta.blackwhitedeliverydriver.dto.StoreIdResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.StoreRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.StoreResponseDto;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.entity.Category;
import com.sparta.blackwhitedeliverydriver.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.security.UserDetailsImpl;
import com.sparta.blackwhitedeliverydriver.service.CategoryService;
import com.sparta.blackwhitedeliverydriver.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/stores")
public class StoreController {

    private final StoreService storeService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public ResponseEntity<?> getStores(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails, Sort sort
    ){
        // 전체 점포 목록 조회
        List<StoreResponseDto> storeResponseDtoPage = storeService.getStores(
                userDetails.getUser(), page - 1, size, sortBy, isAsc
        );

        return ResponseEntity.status(HttpStatus.OK).body(storeResponseDtoPage);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<?> getStoreById(@PathVariable("storeId") UUID storeId){
        StoreResponseDto storeResponseDto = storeService.getStore(storeId);
        return ResponseEntity.status(HttpStatus.OK).body(storeResponseDto);
    }

    @Secured("ROLE_OWNER")
    @GetMapping("/owner")
    public ResponseEntity<?> getStoreOfOwner(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails, Sort sort){
        // OWNER가 등록한 가게 조회
        List<StoreResponseDto> storeResponseDtoPage = storeService.getStoresOfOwner(
                userDetails.getUser(), page - 1, size, sortBy, isAsc
        );
        return ResponseEntity.status(HttpStatus.OK).body(storeResponseDtoPage);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchStores(
            @RequestParam("storeName") String storeName,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc){

        List<StoreResponseDto> storeResponseDtoPage = storeService.searchStores(
                storeName, page -1, size, sortBy, isAsc
        );
        return ResponseEntity.status(HttpStatus.OK).body(storeResponseDtoPage);
    }

    @Secured("ROLE_OWNER")
    @PostMapping("/")
    public ResponseEntity<?> createStore(@Valid @RequestBody StoreRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // OWNER인지 확인 -> 본인 가게 등록
        if(!userDetails.getUser().getRole().equals(UserRoleEnum.OWNER)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Owner 권한이 있어야 점포 생성이 가능합니다.");
        }
        // 카테고리 등록
        List<Category> categoryList = categoryService.getOrCreateCategory(requestDto.getCategory(), userDetails.getUser());

        // 점포 등록
        UUID storeId = storeService.createStore(requestDto, categoryList, userDetails.getUser());

        StoreIdResponseDto storeIdResponseDto = new StoreIdResponseDto(storeId);

        return ResponseEntity.status(HttpStatus.CREATED).body(storeIdResponseDto);
    }
    // Manager, Master -> Owner의 가게 등록

    @Secured("ROLE_OWNER")
    @PutMapping("/{storeId}")
    public ResponseEntity<?> updateStore(@PathVariable UUID storeId, @RequestBody StoreRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // OWNER의 가게인지 확인 -> 본인 가게만 수정
        if(!userDetails.getUser().getRole().equals(UserRoleEnum.OWNER) || !isStoreOfOwner(storeId, userDetails)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인 점포만 수정이 가능합니다.");
        }

        // 카테고리 등록
        List<Category> newCategoryList = categoryService.getOrCreateCategory(requestDto.getCategory(), userDetails.getUser());
        // 점포 수정
        StoreIdResponseDto storeIdResponseDto = new StoreIdResponseDto(storeService.updateStore(storeId, requestDto, newCategoryList, userDetails));

        return ResponseEntity.status(HttpStatus.CREATED).body(storeIdResponseDto);
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<?> deleteStore(@PathVariable UUID storeId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // OWNER의 가게인지 확인 -> 본인 가게만 수정
        if(!userDetails.getUser().getRole().equals(UserRoleEnum.OWNER) || !isStoreOfOwner(storeId, userDetails)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인 점포만 수정이 가능합니다.");
        }

        // 점포 삭제
        storeService.deleteStore(storeId, userDetails);

        StoreIdResponseDto storeIdResponseDto = new StoreIdResponseDto(storeId);

        return ResponseEntity.status(HttpStatus.OK).body(storeIdResponseDto);
    }

    private boolean isStoreOfOwner(UUID storeId, UserDetailsImpl userDetails) {
        String ownerNameOfStore = storeService.getNameOfOwner(storeId);
        if(ownerNameOfStore.matches(userDetails.getUser().getUsername())){ return true; }
        return false;
    }
}
