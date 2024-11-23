package com.sparta.blackwhitedeliverydriver.product.service;

import com.sparta.blackwhitedeliverydriver.category.dto.CreateProductRequestDto;
import com.sparta.blackwhitedeliverydriver.product.dto.ProductIdResponseDto;
import com.sparta.blackwhitedeliverydriver.product.dto.ProductRequestDto;
import com.sparta.blackwhitedeliverydriver.product.dto.ProductResponseDto;
import com.sparta.blackwhitedeliverydriver.product.entity.Product;
import com.sparta.blackwhitedeliverydriver.store.entity.Store;
import com.sparta.blackwhitedeliverydriver.user.entity.User;
import com.sparta.blackwhitedeliverydriver.user.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.user.exception.ExceptionMessage;
import com.sparta.blackwhitedeliverydriver.product.exception.ProductExceptionMessage;
import com.sparta.blackwhitedeliverydriver.store.exception.StoreExceptionMessage;
import com.sparta.blackwhitedeliverydriver.product.repository.ProductRepository;
import com.sparta.blackwhitedeliverydriver.store.repository.StoreRepository;
import com.sparta.blackwhitedeliverydriver.user.repository.UserRepository;
import com.sparta.blackwhitedeliverydriver.common.security.UserDetailsImpl;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ProductService {

    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public Page<ProductResponseDto> getProducts(UUID storeId, int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new NullPointerException(StoreExceptionMessage.STORE_NOT_FOUND.getMessage())
        );
        Page<Product> productList = productRepository.findAllByStoreAndDeletedDateIsNullAndDeletedByIsNull(store, pageable);

        Page<ProductResponseDto> productResponseDtoPage = productList.map(ProductResponseDto::from);

        return productResponseDtoPage;
    }

    @Transactional
    public ProductIdResponseDto createProduct(CreateProductRequestDto requestDto, UserDetails userDetails) {
        Optional<User> curUser = userRepository.findById(userDetails.getUsername());

        if(curUser.get().getRole().equals(UserRoleEnum.OWNER)){
            // 가게 주인이 자신의 가게에 등록
            String nameOfStoreOwner = getNameOfOwner(requestDto.getStoreId());
            if(!nameOfStoreOwner.equals(curUser.get().getUsername())) {
                throw new IllegalArgumentException(StoreExceptionMessage.FORBIDDEN_ACCESS.getMessage());
            }
        }

        // 음식 등록
        Store store = storeRepository.findById(requestDto.getStoreId()).orElseThrow(
                () -> new NullPointerException(StoreExceptionMessage.STORE_NOT_FOUND.getMessage())
        );

        // 같은 가게 동일 음식 확인
        Optional<Product> product = productRepository.findByNameAndStoreStoreId(requestDto.getProductName(), store.getStoreId());
        if(product.isPresent()){
            throw new IllegalArgumentException(ProductExceptionMessage.DUPLICATED_STORE_NAME.getMessage());
        }
        Product newProduct = Product.from(requestDto, store);
        productRepository.save(newProduct);

        ProductIdResponseDto productIdResponseDto = new ProductIdResponseDto(newProduct.getProductId());
        return productIdResponseDto;
    }

    @Transactional
    public ProductIdResponseDto updateProduct(UUID productId, ProductRequestDto requestDto, UserDetailsImpl userDetails) {
        // OWNER의 가게인지 확인 -> 본인 가게만 수정
        UUID storeId = getStoreIdByProductId(productId);
        if(!isStoreOfOwner(storeId, userDetails)){
            throw new IllegalArgumentException(StoreExceptionMessage.FORBIDDEN_ACCESS.getMessage());
        }

        // 음식 조회
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NullPointerException(ProductExceptionMessage.PRODUCT_NOT_FOUND.getMessage())
        );
        product.update(requestDto, userDetails);
        ProductIdResponseDto productIdResponseDto = new ProductIdResponseDto(product.getProductId());

        return productIdResponseDto;
    }

    private boolean isStoreOfOwner(UUID storeId, UserDetailsImpl userDetails) {
        String ownerNameOfStore = getNameOfOwner(storeId);
        Optional<User> curUser = userRepository.findById(userDetails.getUsername());

        if(ownerNameOfStore.matches(curUser.get().getUsername())){ return true; }
        return false;
    }

    public String getNameOfOwner(UUID storeId) {
        User user = storeRepository.findById(storeId).map(Store::getUser).orElseThrow(
                () -> new NullPointerException(StoreExceptionMessage.STORE_NOT_FOUND.getMessage())
        );

        return user.getUsername();
    }

    public UUID getStoreIdByProductId(UUID productId) {
        Store store = productRepository.findById(productId).orElseThrow(
                () -> new NullPointerException(ProductExceptionMessage.PRODUCT_NOT_FOUND.getMessage())
        ).getStore();
        return store.getStoreId();
    }

    @Transactional
    public ProductIdResponseDto deleteProduct(UUID productId, UserDetailsImpl userDetails) {
        Optional<User> curUser = userRepository.findById(userDetails.getUsername());
        Optional<Product> curProduct = productRepository.findById(productId);

        if(curUser.isPresent()){
            if(curUser.get().getRole().equals(UserRoleEnum.OWNER)){
                // 가게 주인이 자신의 가게에 등록
                if(curProduct.isPresent()){
                    String nameOfStoreOwner = getNameOfOwner(curProduct.get().getStore().getStoreId());
                    if(!nameOfStoreOwner.equals(curUser.get().getUsername())) {
                        throw new IllegalArgumentException(StoreExceptionMessage.FORBIDDEN_ACCESS.getMessage());
                    }
                }
                else { throw new NullPointerException(ProductExceptionMessage.PRODUCT_NOT_FOUND.getMessage()); }
            }
        }
        else { throw new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()); }

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NullPointerException(ProductExceptionMessage.PRODUCT_NOT_FOUND.getMessage())
        );

        product.setDeletedDate(LocalDateTime.now());
        product.setDeletedBy(userDetails.getUsername());

        ProductIdResponseDto productIdResponseDto = new ProductIdResponseDto(productId);

        return productIdResponseDto;
    }

}
