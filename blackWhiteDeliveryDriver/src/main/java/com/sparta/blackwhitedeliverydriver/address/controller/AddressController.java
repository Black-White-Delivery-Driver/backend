package com.sparta.blackwhitedeliverydriver.address.controller;

import com.sparta.blackwhitedeliverydriver.address.dto.AddressIdResponseDto;
import com.sparta.blackwhitedeliverydriver.address.dto.AddressRequestDto;
import com.sparta.blackwhitedeliverydriver.address.dto.AddressResponseDto;
import com.sparta.blackwhitedeliverydriver.common.security.UserDetailsImpl;
import com.sparta.blackwhitedeliverydriver.address.service.AddressService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users/address")
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/")
    public ResponseEntity<AddressIdResponseDto> createAddress(@Valid @RequestBody AddressRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 주소 등록 처리
        AddressIdResponseDto addressIdResponseDto = addressService.createAddress(requestDto, userDetails.getUsername());

        // 성공 응답으로 201 Created와 addressIdResponseDto 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(addressIdResponseDto);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressIdResponseDto> updateAddress(@Valid @RequestBody AddressRequestDto requestDto,
                                           @PathVariable UUID addressId,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 주소 수정 처리
        AddressIdResponseDto addressIdResponseDto = addressService.updateAddress(requestDto, addressId, userDetails.getUsername());

        // 성공 응답으로 200 OK와 addressIdResponseDto 반환
        return ResponseEntity.status(HttpStatus.OK).body(addressIdResponseDto);
    }

    @GetMapping("/")
    public ResponseEntity<Page<AddressResponseDto>> getAllAddresses(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 사용자의 모든 주소를 가져온다
        Page<AddressResponseDto> addressResponseDtos = addressService.getAllAddresses(userDetails.getUsername(), page-1, size, sortBy, isAsc);

        // 성공 응답으로 200 OK와 AddressResponseDto 리스트 반환
        return ResponseEntity.status(HttpStatus.OK).body(addressResponseDtos);
    }

    @GetMapping("/current")
    public ResponseEntity<AddressResponseDto> getCurrentAddress(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 사용자의 현재(기본) 배송지를 가져온다
        AddressResponseDto responseDto = addressService.getCurrentAddress(userDetails.getUsername());

        // 성공 응답으로 200 OK와 AddressResponseDto 반환
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/{addressId}/current")
    public ResponseEntity<AddressIdResponseDto> setCurrentAddress(@PathVariable UUID addressId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 사용자의 현재(기본) 배송지를 지정한다
        AddressIdResponseDto addressIdResponseDto = addressService.setCurrentAddress(addressId, userDetails.getUsername());

        // 성공 응답으로 200 OK와 addressIdResponseDto 반환
        return ResponseEntity.status(HttpStatus.OK).body(addressIdResponseDto);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<AddressIdResponseDto> deleteAddress(@PathVariable UUID addressId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 주소 삭제 처리(soft-delete)
        AddressIdResponseDto addressIdResponseDto = addressService.deleteAddress(addressId, userDetails.getUsername());

        // 성공 응답으로 200 OK와 addressIdResponseDto 반환
        return ResponseEntity.status(HttpStatus.OK).body(addressIdResponseDto);
    }
}
