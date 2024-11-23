package com.sparta.blackwhitedeliverydriver.store.service;

import com.sparta.blackwhitedeliverydriver.store.dto.StoreIdResponseDto;
import com.sparta.blackwhitedeliverydriver.store.dto.StoreRequestDto;
import com.sparta.blackwhitedeliverydriver.store.dto.StoreResponseDto;
import com.sparta.blackwhitedeliverydriver.store.dto.StoreByMasterRequestDto;
import com.sparta.blackwhitedeliverydriver.model.category.entity.Category;
import com.sparta.blackwhitedeliverydriver.model.store.entity.Store;
import com.sparta.blackwhitedeliverydriver.model.store.entity.StoreCategory;
import com.sparta.blackwhitedeliverydriver.model.user.entity.User;
import com.sparta.blackwhitedeliverydriver.model.user.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.category.exception.CategoryExceptionMessage;
import com.sparta.blackwhitedeliverydriver.user.exception.ExceptionMessage;
import com.sparta.blackwhitedeliverydriver.store.exception.StoreExceptionMessage;
import com.sparta.blackwhitedeliverydriver.model.category.repository.CategoryRepository;
import com.sparta.blackwhitedeliverydriver.model.store.repository.StoreCategoryRepository;
import com.sparta.blackwhitedeliverydriver.model.store.repository.StoreRepository;
import com.sparta.blackwhitedeliverydriver.model.user.repository.UserRepository;
import com.sparta.blackwhitedeliverydriver.common.security.UserDetailsImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final StoreCategoryRepository storeCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public List<StoreResponseDto> getStores(int page, int size, String sortBy, boolean isAsc) {
        Direction direction = isAsc ? Direction.ASC : Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Store> storeList = storeRepository.findAllByDeletedDateIsNullAndDeletedByIsNullAndIsPublicTrue(pageable);
        List<StoreResponseDto> storeResponseDtoList = new ArrayList<>();

        for(Store store : storeList.getContent()){
            List<StoreCategory> storeCategoryList = storeCategoryRepository.findAllByStoreStoreId(store.getStoreId());
            List<String> categoryNameList = new ArrayList<>();
            for (StoreCategory storeCategory : storeCategoryList){
                Optional<Category> category = Optional.ofNullable(categoryRepository.findById(storeCategory.getCategory().getCategoryId()).orElseThrow(
                        () -> new NullPointerException(CategoryExceptionMessage.CATEGORY_NOT_FOUND.getMessage())
                ));

                categoryNameList.add(category.get().getName());
            }
            String categoryNames = categoryNameList.stream().collect(Collectors.joining(", "));

            StoreResponseDto storeResponseDto = StoreResponseDto.from(store, categoryNames);
            storeResponseDtoList.add(storeResponseDto);
        }

        return storeResponseDtoList;
    }

    public StoreResponseDto getStore(Boolean isExceptDelete, Boolean isPublic, UUID storeId,
                                     UserDetailsImpl userDetails) {
        Store store;
        if(isExceptDelete && isPublic){
            store = storeRepository.findByStoreIdAndDeletedDateIsNullAndDeletedByIsNullAndIsPublicTrue(storeId).orElseThrow(
                    () -> new NullPointerException(StoreExceptionMessage.STORE_NOT_FOUND.getMessage())
            );
        }
        else {
            Optional<User> user = userRepository.findById(userDetails.getUsername());
            if(!user.isPresent()) throw new UsernameNotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage());

            if(user.get().getRole().equals(UserRoleEnum.MANAGER) || user.get().getRole().equals(UserRoleEnum.MASTER)){
                throw new IllegalArgumentException(StoreExceptionMessage.FORBIDDEN_ACCESS.getMessage());
            }

            if(isExceptDelete){
                store = storeRepository.findByStoreIdAndDeletedDateIsNullAndDeletedByIsNull(storeId).orElseThrow(
                        () -> new NullPointerException(StoreExceptionMessage.STORE_NOT_FOUND.getMessage())
                );
            }
            else if(isPublic){
                store = storeRepository.findByStoreIdAndIsPublicTrue(storeId).orElseThrow(
                        () -> new NullPointerException(StoreExceptionMessage.STORE_NOT_FOUND.getMessage())
                );
            }
            else {
                store = storeRepository.findById(storeId).orElseThrow(
                        () -> new NullPointerException(StoreExceptionMessage.STORE_NOT_FOUND.getMessage())
                );
            }
        }

        List<StoreCategory> storeCategoryList = storeCategoryRepository.findAllByStoreStoreId(store.getStoreId());
        List<String> categoryNameList = new ArrayList<>();
        for (StoreCategory storeCategory : storeCategoryList){
            Optional<Category> category = Optional.ofNullable(categoryRepository.findById(storeCategory.getCategory().getCategoryId()).orElseThrow(
                    () -> new NullPointerException(storeCategory.getCategory().getCategoryId() + "라는 카테고리 ID는 없습니다.")
            ));

            categoryNameList.add(category.get().getName());
        }
        String categoryNames = categoryNameList.stream().collect(Collectors.joining(", "));

        return StoreResponseDto.from(store, categoryNames);
    }

    public List<StoreResponseDto> getStoresOfOwner(User user, int page, int size, String sortBy, boolean isAsc) {
        Direction direction = isAsc ? Direction.ASC : Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Optional<User> newUser = Optional.ofNullable(userRepository.findByUsernameAndDeletedDateIsNullAndDeletedByIsNullAndPublicProfileTrue(user.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage())
        ));
        UserRoleEnum userRoleEnum = newUser.get().getRole();

        Page<Store> storeList;
        List<StoreResponseDto> storeResponseDtoList = new ArrayList<>();
        if(userRoleEnum == UserRoleEnum.OWNER) {
            storeList = storeRepository.findAllByUserAndDeletedDateIsNullAndDeletedByIsNullAndIsPublicTrue(newUser.get(), pageable);

            for(Store store : storeList.getContent()){
                List<StoreCategory> storeCategoryList = storeCategoryRepository.findAllByStoreStoreId(store.getStoreId());
                List<String> categoryNameList = new ArrayList<>();
                for (StoreCategory storeCategory : storeCategoryList){
                    Optional<Category> category = Optional.ofNullable(categoryRepository.findById(storeCategory.getCategory().getCategoryId()).orElseThrow(
                            () -> new NullPointerException(CategoryExceptionMessage.CATEGORY_ID_NOT_FOUND.getMessage())
                    ));

                    categoryNameList.add(category.get().getName());
                }
                String categoryNames = categoryNameList.stream().collect(Collectors.joining(", "));

                StoreResponseDto storeResponseDto = StoreResponseDto.from(store, categoryNames);
                storeResponseDtoList.add(storeResponseDto);
            }
        }

        return storeResponseDtoList;
    }

    public List<StoreResponseDto> searchStores(String storeName, int page, int size, String sortBy, boolean isAsc) {
        Direction direction = isAsc ? Direction.ASC : Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Store> storeList = storeRepository.findAllByStoreNameContainingAndDeletedDateIsNullAndDeletedByIsNull(storeName, pageable);
        List<StoreResponseDto> storeResponseDtoList = new ArrayList<>();

        for(Store store : storeList.getContent()){
            List<StoreCategory> storeCategoryList = storeCategoryRepository.findAllByStoreStoreId(store.getStoreId());
            List<String> categoryNameList = new ArrayList<>();
            for (StoreCategory storeCategory : storeCategoryList){
                Optional<Category> category = Optional.ofNullable(categoryRepository.findById(storeCategory.getCategory().getCategoryId()).orElseThrow(
                        () -> new NullPointerException(storeCategory.getCategory().getCategoryId() + "라는 카테고리 ID는 없습니다.")
                ));

                categoryNameList.add(category.get().getName());
            }
            String categoryNames = categoryNameList.stream().collect(Collectors.joining(", "));

            StoreResponseDto storeResponseDto = StoreResponseDto.from(store, categoryNames);
            storeResponseDtoList.add(storeResponseDto);
        }

        return storeResponseDtoList;
    }

    @Transactional
    public StoreIdResponseDto createStore(@Valid StoreRequestDto requestDto, User user) {
        // 점포 중복확인 (이름)
        if(checkStoreName(requestDto.getStoreName())) {
            throw new IllegalArgumentException(StoreExceptionMessage.DUPLICATED_STORE_NAME.getMessage());
        }
        // 전화번호는 같은 사장이 등록하는 경우도 있을거 같음

        Optional<User> newUser = userRepository.findById(user.getUsername());

        // 카테고리 등록
        List<Category> categoryList = getCategoryList(requestDto.getCategory());
        // 점포 등록
        Store store = Store.from(requestDto, newUser.get());
        storeRepository.save(store);
        for(Category category : categoryList) {
            StoreCategory storeCategory = StoreCategory.from(store, category);
            storeCategoryRepository.save(storeCategory);
        }

        StoreIdResponseDto storeIdResponseDto = new StoreIdResponseDto(store.getStoreId());
        return storeIdResponseDto;
    }

    @Transactional
    public StoreIdResponseDto createStoreByMaster(@Valid StoreByMasterRequestDto requestDto, User user) {
        // 점포 중복확인 (이름)
        if(checkStoreName(requestDto.getStoreName())) {
            throw new IllegalArgumentException(StoreExceptionMessage.DUPLICATED_STORE_NAME.getMessage());
        }
        // 전화번호는 같은 사장이 등록하는 경우도 있을거 같음

        Optional<User> newUser = userRepository.findById(requestDto.getUsername());

        // 카테고리 등록
        List<Category> categoryList = getCategoryList(requestDto.getCategory());
        // 점포 등록
        StoreRequestDto storeRequestDto = TransformStoreByMasterRequestDtoToStoreRequestDto(requestDto);
        Store store = Store.from(storeRequestDto, newUser.get());
        storeRepository.save(store);
        for(Category category : categoryList) {
            StoreCategory storeCategory = StoreCategory.from(store, category);
            storeCategoryRepository.save(storeCategory);
        }

        StoreIdResponseDto storeIdResponseDto = new StoreIdResponseDto(store.getStoreId());
        return storeIdResponseDto;
    }

    @Transactional
    public StoreIdResponseDto updateStore(UUID storeId, StoreRequestDto requestDto, UserDetailsImpl userDetails) {
        // OWNER의 가게인지 확인 -> 본인 가게만 수정
        Optional<User> user = userRepository.findById(userDetails.getUsername());
        if(user.get().getRole().equals(UserRoleEnum.OWNER)){
            if(!isStoreOfOwner(storeId, user.get())){
                throw new IllegalArgumentException(StoreExceptionMessage.FORBIDDEN_ACCESS.getMessage());
            }
        }

        // 카테고리 등록
        List<Category> newCategoryList = getCategoryList(requestDto.getCategory());

        // 점포 조회
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new NullPointerException(StoreExceptionMessage.STORE_NOT_FOUND.getMessage())
        );

        // 기존 카테고리 전체 삭제
        storeCategoryRepository.deleteAllByStoreStoreId(storeId);

        // 새로운 카테고리 추가
        Set<Category> categorySet = new HashSet<>();
        for(Category category : newCategoryList) {
            categorySet.add(category);
        }

        // 신규 카테고리는 저장
        List<String> categoryNameList = new ArrayList<>();
        for(Category category : categorySet) {
            categoryNameList.add(category.getName());
            StoreCategory storeCategory = storeCategoryRepository.findByStoreStoreIdAndCategoryCategoryId(store.getStoreId(), category.getCategoryId())
                            .orElseGet(() -> {
                               StoreCategory newStoreCategory = StoreCategory.from(store, category);
                               return storeCategoryRepository.save(newStoreCategory);
                            });
        }
        store.update(requestDto);

        StoreIdResponseDto storeIdResponseDto = new StoreIdResponseDto(store.getStoreId());
        return storeIdResponseDto;
    }

    @Transactional
    public StoreIdResponseDto updateStoreByMaster(UUID storeId, StoreByMasterRequestDto requestDto, UserDetailsImpl userDetails) {
        // OWNER의 가게인지 확인 -> 본인 가게만 수정
        Optional<User> ownerUser = userRepository.findById(requestDto.getUsername());
        if(!isStoreOfOwner(storeId, ownerUser.get())){
            throw new IllegalArgumentException(StoreExceptionMessage.FORBIDDEN_ACCESS.getMessage());
        }
        // 카테고리 등록
        List<Category> newCategoryList = getCategoryList(requestDto.getCategory());

        // 점포 조회
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new NullPointerException(StoreExceptionMessage.STORE_NOT_FOUND.getMessage())
        );

        // 기존 카테고리 전체 삭제
        storeCategoryRepository.deleteAllByStoreStoreId(storeId);

        // 새로운 카테고리 추가
        Set<Category> categorySet = new HashSet<>();
        for(Category category : newCategoryList) {
            categorySet.add(category);
        }

        // 신규 카테고리는 저장
        List<String> categoryNameList = new ArrayList<>();
        for(Category category : categorySet) {
            categoryNameList.add(category.getName());
            StoreCategory storeCategory = storeCategoryRepository.findByStoreStoreIdAndCategoryCategoryId(store.getStoreId(), category.getCategoryId())
                    .orElseGet(() -> {
                        StoreCategory newStoreCategory = StoreCategory.from(store, category);
                        return storeCategoryRepository.save(newStoreCategory);
                    });
        }
        store.updateByMaster(requestDto, userDetails);

        StoreIdResponseDto storeIdResponseDto = new StoreIdResponseDto(store.getStoreId());
        return storeIdResponseDto;
    }

    @Transactional
    public StoreIdResponseDto deleteStore(UUID storeId, UserDetailsImpl userDetails) {
        // OWNER의 가게인지 확인 -> 본인 가게만 삭제
        Optional<User> user = userRepository.findById(userDetails.getUsername());
        if(user.get().getRole().equals(UserRoleEnum.OWNER)){
            // OWNER라면 주인인지 확인
            if(!isStoreOfOwner(storeId, user.get())){
                throw new IllegalArgumentException(StoreExceptionMessage.FORBIDDEN_ACCESS.getMessage());
            }
        }

        User newUser = storeRepository.findById(storeId).get().getUser();

        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new NullPointerException(StoreExceptionMessage.STORE_NOT_FOUND.getMessage())
        );

        store.setDeletedDate(LocalDateTime.now());
        store.setDeletedBy(newUser.getUsername());

        return new StoreIdResponseDto(store.getStoreId());
    }

    private List<Category> getCategoryList(String categoryNames) {
        Set<String> categorySet = new HashSet<>();
        Arrays.stream(categoryNames.split(","))
                .map(String::trim)
                .forEach(categorySet::add);

        List<Category> categoryList = new ArrayList<>();
        for(String categoryName : categorySet) {
            Category category = categoryRepository.findByName(categoryName).orElseThrow(
                    () -> new NullPointerException(CategoryExceptionMessage.CATEGORY_ID_NOT_FOUND.getMessage()));
            categoryList.add(category);
        }

        return categoryList;
    }

    private boolean isStoreOfOwner(UUID storeId, User user) {
        User storeOwnerInfo = storeRepository.findById(storeId).get().getUser();
        Optional<User> loginUserInfo = userRepository.findById(user.getUsername());

        if(storeOwnerInfo.getUsername().matches(loginUserInfo.get().getUsername())){ return true; }
        return false;
    }

    private StoreRequestDto TransformStoreByMasterRequestDtoToStoreRequestDto(@Valid StoreByMasterRequestDto requestDto) {
        StoreRequestDto storeRequestDto = new StoreRequestDto();
        storeRequestDto.setStoreName(requestDto.getStoreName());
        storeRequestDto.setPhoneNumber(requestDto.getPhoneNumber());
        storeRequestDto.setOpenTime(requestDto.getOpenTime());
        storeRequestDto.setCloseTime(requestDto.getCloseTime());
        storeRequestDto.setImgUrl(requestDto.getImgUrl());
        storeRequestDto.setZipNum(requestDto.getZipNum());
        storeRequestDto.setCity(requestDto.getCity());
        storeRequestDto.setDistrict(requestDto.getDistrict());
        storeRequestDto.setStreetName(requestDto.getStreetName());
        storeRequestDto.setStreetNumber(requestDto.getStreetNumber());
        storeRequestDto.setDetailAddr(requestDto.getDetailAddr());
        storeRequestDto.setStoreIntro(requestDto.getStoreIntro());
        storeRequestDto.setCategory(requestDto.getCategory());
        return storeRequestDto;
    }

    private Boolean checkStoreName(@NotBlank String storeName) {
        Optional<Store> store = storeRepository.findByStoreName(storeName);
        if(store.isPresent()) { return true; }
        return false;
    }

    @Transactional
    public StoreIdResponseDto publicSwitch(UUID storeId) {
        Optional<Store> store = storeRepository.findById(storeId);
        if(!store.isPresent()){throw new NullPointerException(StoreExceptionMessage.STORE_NOT_FOUND.getMessage());}

        store.get().updatePublic(store.get().getIsPublic());

        StoreIdResponseDto storeIdResponseDto = new StoreIdResponseDto(store.get().getStoreId());

        return storeIdResponseDto;
    }
}
