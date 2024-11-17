package com.sparta.blackwhitedeliverydriver.service;

import com.sparta.blackwhitedeliverydriver.dto.StoreIdResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.StoreRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.StoreResponseDto;
import com.sparta.blackwhitedeliverydriver.entity.Category;
import com.sparta.blackwhitedeliverydriver.entity.Store;
import com.sparta.blackwhitedeliverydriver.entity.StoreCategory;
import com.sparta.blackwhitedeliverydriver.entity.User;
import com.sparta.blackwhitedeliverydriver.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.exception.CategoryExceptionMessage;
import com.sparta.blackwhitedeliverydriver.exception.ExceptionMessage;
import com.sparta.blackwhitedeliverydriver.exception.StoreExceptionMessage;
import com.sparta.blackwhitedeliverydriver.repository.CategoryRepository;
import com.sparta.blackwhitedeliverydriver.repository.StoreCategoryRepository;
import com.sparta.blackwhitedeliverydriver.repository.StoreRepository;
import com.sparta.blackwhitedeliverydriver.repository.UserRepository;
import com.sparta.blackwhitedeliverydriver.security.UserDetailsImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final StoreCategoryRepository storeCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public StoreIdResponseDto createStore(@Valid StoreRequestDto requestDto, User user) {
        // 점포 중복확인 (이름)
        Optional<Store> requestStoreName = storeRepository.findByStoreName(requestDto.getStoreName());
        if(checkStoreName(requestStoreName.get().getStoreName())) {
            throw new IllegalArgumentException(StoreExceptionMessage.DUPLICATED_STORE_NAME.getMessage());
        }
        // 전화번호는 같은 사장이 등록하는 경우도 있을거 같음

        // 카테고리 등록
        List<Category> categoryList = getCategoryList(requestDto.getCategory());
        // 점포 등록
        Store store = Store.from(requestDto, user);
        storeRepository.save(store);
        for(Category category : categoryList) {
            StoreCategory storeCategory = StoreCategory.from(store, category);
            storeCategoryRepository.save(storeCategory);
        }

        StoreIdResponseDto storeIdResponseDto = new StoreIdResponseDto(store.getStoreId());
        return storeIdResponseDto;
    }

    private Boolean checkStoreName(@NotBlank String storeName) {
        Optional<Store> store = storeRepository.findByStoreName(storeName);
        if(store.isPresent()) { return true; }
        return false;
    }


    @Transactional
    public StoreIdResponseDto updateStore(UUID storeId, StoreRequestDto requestDto, UserDetailsImpl userDetails) {
        // OWNER의 가게인지 확인 -> 본인 가게만 수정
        if(!isStoreOfOwner(storeId, userDetails)){
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
        store.update(requestDto, userDetails);

        StoreIdResponseDto storeIdResponseDto = new StoreIdResponseDto(store.getStoreId());
        return storeIdResponseDto;
    }

    public StoreResponseDto getStore(UUID storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new NullPointerException("해당 점포를 찾을 수 없습니다.")
        );

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

    @Transactional
    public StoreIdResponseDto deleteStore(UUID storeId, UserDetailsImpl userDetails) {
        // OWNER의 가게인지 확인 -> 본인 가게만 삭제
        if(!isStoreOfOwner(storeId, userDetails)){
            throw new IllegalArgumentException(StoreExceptionMessage.FORBIDDEN_ACCESS.getMessage());
        }

        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new NullPointerException(StoreExceptionMessage.STORE_NOT_FOUND.getMessage())
        );

        store.setDeletedDate(LocalDateTime.now());
        store.setDeletedBy(userDetails.getUsername());

        return new StoreIdResponseDto(store.getStoreId());
    }

    public String getNameOfOwner(UUID storeId) {
        User user = storeRepository.findById(storeId).map(Store::getUser).orElseThrow(
                () -> new NullPointerException(StoreExceptionMessage.STORE_NOT_FOUND.getMessage())
        );

        return user.getUsername();
    }

    public List<StoreResponseDto> getStores(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Store> storeList = storeRepository.findAll(pageable);
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

    public List<StoreResponseDto> getStoresOfOwner(User user, int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Optional<User> newUser = Optional.ofNullable(userRepository.findById(user.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException(ExceptionMessage.USER_NOT_FOUND.getMessage())
        ));
        UserRoleEnum userRoleEnum = newUser.get().getRole();

        Page<Store> storeList;
        List<StoreResponseDto> storeResponseDtoList = new ArrayList<>();
        if(userRoleEnum == UserRoleEnum.OWNER) {
            storeList = storeRepository.findAllByUser(user, pageable);

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
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Store> storeList = storeRepository.findAllByStoreNameContaining(storeName, pageable);
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

    private boolean isStoreOfOwner(UUID storeId, UserDetailsImpl userDetails) {
        User storeOwnerInfo = storeRepository.findById(storeId).get().getUser();
        Optional<User> loginUserInfo = userRepository.findById(userDetails.getUsername());

        if(storeOwnerInfo.getUsername().matches(loginUserInfo.get().getUsername())){ return true; }
        return false;
    }
}
