package com.sparta.blackwhitedeliverydriver.address.service;

import com.sparta.blackwhitedeliverydriver.address.dto.AddressIdResponseDto;
import com.sparta.blackwhitedeliverydriver.address.dto.AddressRequestDto;
import com.sparta.blackwhitedeliverydriver.address.dto.AddressResponseDto;
import com.sparta.blackwhitedeliverydriver.model.address.entity.Address;
import com.sparta.blackwhitedeliverydriver.model.user.entity.User;
import com.sparta.blackwhitedeliverydriver.user.exception.ExceptionMessage;
import com.sparta.blackwhitedeliverydriver.model.address.repository.AddressRepository;
import com.sparta.blackwhitedeliverydriver.model.user.repository.UserRepository;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    public AddressIdResponseDto createAddress(@Valid AddressRequestDto requestDto, String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        Address address = Address.from(requestDto, user);
        addressRepository.save(address);

        return new AddressIdResponseDto(address.getId());
    }

    @Transactional
    public AddressIdResponseDto updateAddress(@Valid AddressRequestDto requestDto, UUID addressId, String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.ADDRESS_NOT_FOUND.getMessage()));

        checkDeletedAddress(address);
        checkCreatedBy(address, user.getUsername());

        address.update(requestDto);
        addressRepository.save(address);

        return new AddressIdResponseDto(address.getId());
    }

    public Page<AddressResponseDto> getAllAddresses(String username, int page, int size, String sortBy, boolean isAsc) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        // 페이징 처리
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // 페이징이 적용된 Address 리스트를 가져온다
        Page<Address> addressPage = addressRepository.findAllByUserAndDeletedByIsNullAndDeletedDateIsNull(user, pageable);

        return addressPage.map(AddressResponseDto::from);
    }

    public AddressResponseDto getCurrentAddress(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        checkCurrentAddress(user);

        Address address = addressRepository.findById(user.getCurrentAddress().getId())
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.ADDRESS_NOT_FOUND.getMessage()));

        return AddressResponseDto.from(address);
    }

    @Transactional
    public AddressIdResponseDto setCurrentAddress(UUID addressId, String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.ADDRESS_NOT_FOUND.getMessage()));

        checkCreatedBy(address, user.getUsername());

        user.updateCurrentAddress(address);
        userRepository.save(user);

        return new AddressIdResponseDto(address.getId());
    }

    @Transactional
    public AddressIdResponseDto deleteAddress(UUID addressId, String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.ADDRESS_NOT_FOUND.getMessage()));

        checkDeletedAddress(address);
        checkCreatedBy(address, user.getUsername());

        // 삭제하는 배송지가 사용자의 현재(기본)배송지라면 현재(기본)배송지를 null로 처리
        if (user.getCurrentAddress().equals(address)) {
            user.updateCurrentAddress(null);
        }

        address.setDeletedBy(user.getUsername());
        address.setDeletedDate(LocalDateTime.now());

        addressRepository.save(address);

        return new AddressIdResponseDto(address.getId());
    }

    private void checkDeletedAddress(Address address) {
        if (address.getDeletedDate() != null || address.getDeletedBy() != null) {
            throw new IllegalArgumentException(ExceptionMessage.ADDRESS_DELETED.getMessage());
        }
    }

    private void checkCurrentAddress(User user) {
        if (user.getCurrentAddress() == null) {
            throw new IllegalArgumentException(ExceptionMessage.CURRNET_ADDRESS_NOT_FOUND.getMessage());
        }
    }

    private void checkCreatedBy(Address address, String username) {
        if (!address.getCreatedBy().equals(username)) {
            throw new AccessDeniedException(ExceptionMessage.NOT_ALLOWED_API.getMessage());
        }
    }

}
