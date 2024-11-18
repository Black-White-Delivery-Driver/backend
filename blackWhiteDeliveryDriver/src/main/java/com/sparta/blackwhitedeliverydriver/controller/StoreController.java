package com.sparta.blackwhitedeliverydriver.controller;

import com.sparta.blackwhitedeliverydriver.dto.StoreIdResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.StoreRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.StoreResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.StoreByMasterRequestDto;
import com.sparta.blackwhitedeliverydriver.security.UserDetailsImpl;
import com.sparta.blackwhitedeliverydriver.service.StoreService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("api/v1/stores")
public class StoreController {

    private final StoreService storeService;

    @GetMapping("/")
    public ResponseEntity<?> getStores(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "true") boolean isAsc,
            Sort sort
    ){
        // 전체 점포 목록 조회
        List<StoreResponseDto> storeResponseDtoPage = storeService.getStores(
                page - 1, size, sortBy, isAsc
        );

        return ResponseEntity.status(HttpStatus.OK).body(storeResponseDtoPage);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<?> getStoreById(
            @RequestParam(value = "isExceptDelete", defaultValue = "true") Boolean isExceptDelete,
            @RequestParam(value = "isPublic", defaultValue = "true") Boolean isPublic,
            @PathVariable("storeId") UUID storeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails, Sort sort
    ){
        StoreResponseDto storeResponseDto = storeService.getStore(isExceptDelete, isPublic, storeId, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(storeResponseDto);
    }

    @Secured({"ROLE_MANAGER", "ROLE_MASTER"})
    @GetMapping("/{storeId}/public-switch")
    public ResponseEntity<?> publicSwitch(
            @PathVariable("storeId") UUID storeId
    ){
        StoreIdResponseDto storeIdResponseDto = storeService.publicSwitch(storeId);
        return ResponseEntity.status(HttpStatus.OK).body(storeIdResponseDto);
    }

    @Secured("ROLE_OWNER")
    @GetMapping("/owner")
    public ResponseEntity<?> getStoreOfOwner(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "true") boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails, Sort sort
    ){
        // OWNER가 등록한 가게 조회
        List<StoreResponseDto> storeResponseDtoPage = storeService.getStoresOfOwner(
                userDetails.getUser(), page - 1, size, sortBy, isAsc
        );
        return ResponseEntity.status(HttpStatus.OK).body(storeResponseDtoPage);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchStores(
            @RequestParam("storeName") String storeName,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "true") boolean isAsc
    ){

        List<StoreResponseDto> storeResponseDtoPage = storeService.searchStores(
                storeName, page -1, size, sortBy, isAsc
        );
        return ResponseEntity.status(HttpStatus.OK).body(storeResponseDtoPage);
    }

    @Secured("ROLE_OWNER")
    @PostMapping("/")
    public ResponseEntity<?> createStore(@Valid @RequestBody StoreRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 점포 등록
        StoreIdResponseDto storeIdResponseDto = storeService.createStore(requestDto, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(storeIdResponseDto);
    }

    @Secured({"ROLE_MANAGER", "ROLE_MASTER"})
    @PostMapping("/master")
    public ResponseEntity<?> createStoreByMaster(@Valid @RequestBody StoreByMasterRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 점포 등록
        StoreIdResponseDto storeIdResponseDto = storeService.createStoreByMaster(requestDto, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(storeIdResponseDto);
    }
    // Manager, Master -> Owner의 가게 등록 => is_public 업데이트, Owner 정보 대신 입력

    @Secured("ROLE_OWNER")
    @PutMapping("/{storeId}")
    public ResponseEntity<?> updateStore(@PathVariable UUID storeId, @RequestBody StoreRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 점포 수정
        StoreIdResponseDto storeIdResponseDto = storeService.updateStore(storeId, requestDto, userDetails);

        return ResponseEntity.status(HttpStatus.CREATED).body(storeIdResponseDto);
    }

    @Secured({"ROLE_MANAGER", "ROLE_MASTER"})
    @PutMapping("/{storeId}/master")
    public ResponseEntity<?> updateStoreByMaster(
            @PathVariable UUID storeId,
            @RequestBody StoreByMasterRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {// Manager, Master -> Owner의 가게 수정
        // 점포 수정
        StoreIdResponseDto storeIdResponseDto = storeService.updateStoreByMaster(storeId, requestDto, userDetails);

        return ResponseEntity.status(HttpStatus.CREATED).body(storeIdResponseDto);
    }

    @Secured({"ROLE_OWNER","ROLE_MANAGER","ROLE_MASTER"})
    @DeleteMapping("/{storeId}")
    public ResponseEntity<?> deleteStore(@PathVariable UUID storeId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 점포 삭제
        StoreIdResponseDto storeIdResponseDto = storeService.deleteStore(storeId, userDetails);;

        return ResponseEntity.status(HttpStatus.OK).body(storeIdResponseDto);
    }
}
