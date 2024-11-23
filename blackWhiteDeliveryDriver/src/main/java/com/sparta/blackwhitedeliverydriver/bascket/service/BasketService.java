package com.sparta.blackwhitedeliverydriver.bascket.service;

import com.sparta.blackwhitedeliverydriver.bascket.dto.BasketAddRequestDto;
import com.sparta.blackwhitedeliverydriver.bascket.dto.BasketGetResponseDto;
import com.sparta.blackwhitedeliverydriver.bascket.dto.BasketResponseDto;
import com.sparta.blackwhitedeliverydriver.bascket.dto.BasketUpdateRequestDto;
import com.sparta.blackwhitedeliverydriver.model.bascket.entity.Basket;
import com.sparta.blackwhitedeliverydriver.model.product.entity.Product;
import com.sparta.blackwhitedeliverydriver.model.user.entity.User;
import com.sparta.blackwhitedeliverydriver.model.user.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.bascket.exception.BasketExceptionMessage;
import com.sparta.blackwhitedeliverydriver.user.exception.ExceptionMessage;
import com.sparta.blackwhitedeliverydriver.order.exception.OrderExceptionMessage;
import com.sparta.blackwhitedeliverydriver.model.bascket.repository.BasketRepository;
import com.sparta.blackwhitedeliverydriver.model.order.repository.OrderRepository;
import com.sparta.blackwhitedeliverydriver.model.product.repository.ProductRepository;
import com.sparta.blackwhitedeliverydriver.model.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasketService {

    private final BasketRepository basketRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public BasketResponseDto addProductToBasket(String username, BasketAddRequestDto request) {
        // 유저가 유효성 체크
        User user = checkValidUser(username);

        //활성화 주문 체크
        checkCreatedOrderByUser(user);

        // 상품이 유효성
        Product product = checkValidProduct(request.getProductId());

        // 담은 상품이 중복된 상품인지 체크
        List<Basket> baskets = basketRepository.findAllByUserAndNotDeleted(user);
        checkDuplicatedProduct(product, baskets);

        // 담은 상품의 지점이 장바구니 상품의 지점과 다른지 체크
        checkProductStore(product, baskets);

        Basket basket = Basket.ofUserAndStoreAndRequest(user, product.getStore(), product, request);

        basket = basketRepository.save(basket);

        return BasketResponseDto.fromBasket(basket);
    }


    @Transactional
    public BasketResponseDto removeProductFromBasket(String username, UUID basketId) {
        //유저 유효성 검사
        User user = checkValidUser(username);

        //장바구니 유효성 검사
        Basket basket = checkValidBasket(basketId);

        //장바구니 유저 유효성 검사
        checkBasketUser(user, basket);

        basket.softDelete(username, LocalDateTime.now());
        basketRepository.save(basket);

        return BasketResponseDto.fromBasket(basket);
    }

    public Page<BasketGetResponseDto> getBaskets(String username, int page, int size, String sortBy, boolean isAsc) {
        // 유저 유효성 검증
        User user = checkValidUser(username);

        //페이징
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Basket> baskets;
        if (user.getRole().equals(UserRoleEnum.CUSTOMER)) {
            baskets = basketRepository.findAllByUserAndNotDeleted(user, pageable);
        } else {
            baskets = basketRepository.findAll(pageable);
        }

        return baskets.map(BasketGetResponseDto::fromBasket);
    }

    @Transactional
    public BasketResponseDto updateBasket(String username, BasketUpdateRequestDto request) {
        //유저 유효성 검사
        User user = checkValidUser(username);

        //장바구니 유효성 검사
        Basket basket = checkValidBasket(request.getBasketId());

        //장바구니 유저와 api 호출 유저 체크
        checkBasketUser(user, basket);

        basket.updateBasketOfQuantity(request.getQuantity());
        basketRepository.save(basket);

        return BasketResponseDto.fromBasket(basket);
    }

    public Page<BasketGetResponseDto> searchBasketsByProductName(String username, String productName, int page,
                                                                 int size, String sortBy, boolean isAsc) {
        //유저 유효성 검사
        User user = checkValidUser(username);

        // 페이징과 정렬 정보 생성
        //페이징
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // 데이터 조회 및 변환
        Page<Basket> baskets = basketRepository.findByProductNameContainingAndUserAndNotDeleted(productName, user,
                pageable);
        return baskets.map(BasketGetResponseDto::fromBasket);
    }

    private void checkBasketUser(User user, Basket basket) {
        if (!user.getUsername().equals(basket.getUser().getUsername())) {
            throw new IllegalArgumentException(BasketExceptionMessage.BASKET_USER_NOT_EQUALS.getMessage());
        }
    }

    private void checkDuplicatedProduct(Product product, List<Basket> baskets) {
        for (Basket basket : baskets) {
            UUID productId = basket.getProduct().getProductId();
            if (productId.equals(product.getProductId())) {
                throw new IllegalArgumentException(BasketExceptionMessage.BASKET_DUPLICATED.getMessage());
            }
        }
    }

    private void checkProductStore(Product product, List<Basket> baskets) {
        for (Basket basket : baskets) {
            UUID storeId = basket.getProduct().getStore().getStoreId();
            if (!storeId.equals(product.getStore().getStoreId())) {
                throw new IllegalArgumentException(BasketExceptionMessage.BASKET_DIFFERENT_STORE.getMessage());
            }
        }
    }

    public User checkValidUser(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));

        if (user.getDeletedDate() != null || user.getDeletedBy() != null) {
            throw new NullPointerException(ExceptionMessage.USER_DELETED.getMessage());
        }

        return user;
    }

    private Product checkValidProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NullPointerException("상품을 찾을 수 없습니다."));
        if (product.getDeletedDate() != null || product.getDeletedBy() != null) {
            throw new NullPointerException("삭제된 상품입니다.");
        }
        if (!product.getIsPublic()) {
            throw new NullPointerException("비공개 상품입니다.");
        }
        return product;
    }

    private Basket checkValidBasket(UUID basketId) {
        Basket basket = basketRepository.findById(basketId).orElseThrow(() ->
                new NullPointerException(BasketExceptionMessage.BASKET_NOT_FOUND.getMessage()));
        if (basket.getDeletedDate() != null || basket.getDeletedBy() != null) {
            throw new NullPointerException(BasketExceptionMessage.BASKET_DELETE.getMessage());
        }
        return basket;
    }

    private void checkCreatedOrderByUser(User user) {
        orderRepository.findActiveOrderByUser(user)
                .ifPresent(order -> {
                    throw new IllegalArgumentException(OrderExceptionMessage.ORDER_ALREADY_EXIST.getMessage());
                });
    }
}
