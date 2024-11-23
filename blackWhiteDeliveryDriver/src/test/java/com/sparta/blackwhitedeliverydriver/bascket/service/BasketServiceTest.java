package com.sparta.blackwhitedeliverydriver.bascket.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sparta.blackwhitedeliverydriver.bascket.dto.BasketAddRequestDto;
import com.sparta.blackwhitedeliverydriver.bascket.dto.BasketResponseDto;
import com.sparta.blackwhitedeliverydriver.bascket.entity.Basket;
import com.sparta.blackwhitedeliverydriver.order.entity.Order;
import com.sparta.blackwhitedeliverydriver.product.entity.Product;
import com.sparta.blackwhitedeliverydriver.store.entity.Store;
import com.sparta.blackwhitedeliverydriver.user.entity.User;
import com.sparta.blackwhitedeliverydriver.user.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.bascket.exception.BasketExceptionMessage;
import com.sparta.blackwhitedeliverydriver.user.exception.ExceptionMessage;
import com.sparta.blackwhitedeliverydriver.order.exception.OrderExceptionMessage;
import com.sparta.blackwhitedeliverydriver.bascket.repository.BasketRepository;
import com.sparta.blackwhitedeliverydriver.order.repository.OrderRepository;
import com.sparta.blackwhitedeliverydriver.product.repository.ProductRepository;
import com.sparta.blackwhitedeliverydriver.store.repository.StoreRepository;
import com.sparta.blackwhitedeliverydriver.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BasketServiceTest {
    BasketService basketService;
    BasketRepository basketRepository = mock(BasketRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    StoreRepository storeRepository = mock(StoreRepository.class);
    ProductRepository productRepository = mock(ProductRepository.class);
    OrderRepository orderRepository = mock(OrderRepository.class);

    @BeforeEach
    public void setUp() {
        basketService = new BasketService(basketRepository, userRepository, productRepository, orderRepository);
    }

    @Test
    @DisplayName("장바구니 담기 성공")
    void addProductToBasket_success() {
        //given
        String username = "user";
        String storeName = "store";
        UUID basketId = UUID.randomUUID();
        UUID basketId2 = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        int quantity = 2;
        Store store = Store.builder()
                .storeId(storeId)
                .storeName(storeName)
                .build();
        Product product = Product.builder()
                .productId(productId)
                .isPublic(true)
                .store(store)
                .build();
        Product product2 = Product.builder()
                .productId(productId2)
                .isPublic(true)
                .store(store)
                .build();
        Basket basket = Basket.builder()
                .id(basketId)
                .product(product)
                .build();
        Basket basket2 = Basket.builder()
                .id(basketId2)
                .product(product2)
                .build();
        User user = User.builder()
                .role(UserRoleEnum.CUSTOMER)
                .username(username)
                .build();
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(productId2)
                .quantity(quantity)
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        given(storeRepository.findById(any())).willReturn(Optional.ofNullable(store));
        given(productRepository.findById(any())).willReturn(Optional.ofNullable(product2));
        when(basketRepository.findAllByUser(any())).thenReturn(List.of(basket));
        when(basketRepository.save(any())).thenReturn(basket2);

        //when
        BasketResponseDto response = basketService.addProductToBasket(username, request);

        //then
        Assertions.assertEquals(basketId2, response.getBasketId());
    }

    @Test
    @DisplayName("장바구니 담기 실패1 : 유저가 존재하지 않는 경우")
    void addProductToBasket_fail1() {
        //given
        String username = "user1";
        UUID productId = UUID.randomUUID();
        int quantity = 2;
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(productId)
                .quantity(quantity)
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        //when & then
        Exception exception = assertThrows(NullPointerException.class,
                () -> basketService.addProductToBasket(username, request));
        assertEquals(ExceptionMessage.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("장바구니 담기 실패2 : 유저가 삭제된 경우")
    void addProductToBasket_fail2() {
        //given
        User user = User.builder()
                .username("user")
                .build();
        user.setDeletedDate(LocalDateTime.now());

        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(UUID.randomUUID())
                .quantity(2)
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        //when & then
        Exception exception = assertThrows(NullPointerException.class,
                () -> basketService.addProductToBasket(user.getUsername(), request));
        assertEquals(ExceptionMessage.USER_DELETED.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("장바구니 담기 실패3 : 상품이 없는 경우")
    void addProductToBasket_fail3() {
        //given
        User user = User.builder()
                .username("user")
                .build();
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(UUID.randomUUID())
                .quantity(2)
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        when(storeRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(NullPointerException.class,
                () -> {
                    assert user != null;
                    basketService.addProductToBasket(user.getUsername(), request);
                });
        Assertions.assertEquals("상품을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("장바구니 담기 실패4 : 상품이 삭제된 경우")
    void addProductToBasket_fail4() {
        //given
        User user = User.builder()
                .username("user")
                .build();
        Product product = Product.builder()
                .productId(UUID.randomUUID())
                .build();
        product.setDeletedDate(LocalDateTime.now());
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(UUID.randomUUID())
                .quantity(2)
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        //when & then
        Exception exception = assertThrows(NullPointerException.class,
                () -> {
                    assert user != null;
                    basketService.addProductToBasket(user.getUsername(), request);
                });
        assertEquals(exception.getMessage(), "삭제된 상품입니다.");
    }

    @Test
    @DisplayName("장바구니 담기 실패5 : 상품이 비공개된 경우")
    void addProductToBasket_fail5() {
        //given
        User user = User.builder()
                .username("user")
                .build();
        Product product = Product.builder()
                .productId(UUID.randomUUID())
                .isPublic(false)
                .build();
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(UUID.randomUUID())
                .quantity(2)
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        //when & then
        Exception exception = assertThrows(NullPointerException.class,
                () -> {
                    assert user != null;
                    basketService.addProductToBasket(user.getUsername(), request);
                });
        assertEquals(exception.getMessage(), "비공개 상품입니다.");
    }

    @Test
    @DisplayName("장바구니 담기 실패6 : 상품이 증복된 경우")
    void addProductToBasket_fail6() {
        //given
        Product product = Product.builder()
                .productId(UUID.randomUUID())
                .isPublic(true)
                .build();
        Basket basket = Basket.builder()
                .product(product)
                .build();
        User user = User.builder()
                .username("user")
                .build();
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(product.getProductId())
                .quantity(2)
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        given(productRepository.findById(any())).willReturn(Optional.of(product));
        when(basketRepository.findAllByUserAndNotDeleted(any())).thenReturn(List.of(basket));

        //when & then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> {
                    assert user != null;
                    basketService.addProductToBasket(user.getUsername(), request);
                });
        assertEquals(BasketExceptionMessage.BASKET_DUPLICATED.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("장바구니 담기 실패7 : 점포이 다른 경우")
    void addProductToBasket_fail7() {
        //given
        Store store = Store.builder()
                .storeId(UUID.randomUUID())
                .build();
        Store store2 = Store.builder()
                .storeId(UUID.randomUUID())
                .build();
        Product product = Product.builder()
                .productId(UUID.randomUUID())
                .store(store)
                .isPublic(true)
                .build();
        Product product2 = Product.builder()
                .productId(UUID.randomUUID())
                .store(store2)
                .isPublic(true)
                .build();
        Basket basket = Basket.builder()
                .id(UUID.randomUUID())
                .product(product)
                .build();
        User user = User.builder()
                .username("user")
                .build();
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(product2.getProductId())
                .quantity(2)
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        given(storeRepository.findById(any())).willReturn(Optional.ofNullable(store));
        given(productRepository.findById(any())).willReturn(Optional.of(product2));
        when(basketRepository.findAllByUserAndNotDeleted(any())).thenReturn(List.of(basket));

        //when & then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> {
                    assert user != null;
                    basketService.addProductToBasket(user.getUsername(), request);
                });
        assertEquals(BasketExceptionMessage.BASKET_DIFFERENT_STORE.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("장바구니 담기 실패8 : CREATE 주문이 있는 경우")
    void addProductToBasketToBasket_fail8() {
        //given
        User user = User.builder()
                .username("user")
                .build();
        Order order = Order.builder()
                .id(UUID.randomUUID())
                .build();

        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        when(orderRepository.findActiveOrderByUser(any())).thenReturn(Optional.ofNullable(order));

        //when & then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> {
                    assert user != null;
                    basketService.addProductToBasket(user.getUsername(), request);
                });
        assertEquals(OrderExceptionMessage.ORDER_ALREADY_EXIST.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("장바구니 빼기 성공")
    void removeProductFromBasket_success() {
        //given;
        User user = User.builder()
                .username("user")
                .build();
        Store store = Store.builder()
                .storeId(UUID.randomUUID())
                .storeName("store")
                .build();
        Basket basket = Basket.builder()
                .id(UUID.randomUUID())
                .store(store)
                .quantity(2)
                .user(user)
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        given(basketRepository.findById(any())).willReturn(Optional.ofNullable(basket));

        //when
        assert user != null;
        assert basket != null;
        BasketResponseDto response = basketService.removeProductFromBasket(user.getUsername(), basket.getId());

        //then
        Assertions.assertEquals(basket.getId(), response.getBasketId());
    }


    @Test
    @DisplayName("장바구니 빼기 실패1 : 유저가 존재하지 않는 경우")
    void removeProductFromBasket_fail1() {
        //given
        given(userRepository.findById(any())).willReturn(Optional.empty());

        //when & then
        Exception exception = assertThrows(NullPointerException.class,
                () -> basketService.removeProductFromBasket("user", UUID.randomUUID()));
        assertEquals(ExceptionMessage.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("장바구니 빼기 실패2 : 삭제된 유저인 경우")
    void removeProductFromBasket_fail2() {
        //given
        User user = User.builder()
                .username("user")
                .build();
        user.setDeletedDate(LocalDateTime.now());

        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        //when & then
        Exception exception = assertThrows(NullPointerException.class,
                () -> basketService.removeProductFromBasket(user.getUsername(), UUID.randomUUID()));
        assertEquals(ExceptionMessage.USER_DELETED.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("장바구니 빼기 실패3 : 장바구니가 없는 경우")
    void removeProductFromBasket_fail3() {
        //given
        User user = User.builder()
                .username("user")
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        given(basketRepository.findById(any())).willReturn(Optional.empty());

        //when & then
        Exception exception = assertThrows(NullPointerException.class,
                () -> {
                    assert user != null;
                    basketService.removeProductFromBasket(user.getUsername(), UUID.randomUUID());
                });
        assertEquals(BasketExceptionMessage.BASKET_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("장바구니 빼기 실패4 : 삭제된 장바구니인 경우")
    void removeProductFromBasket_fail4() {
        //given
        User user = User.builder()
                .username("user")
                .build();
        Basket basket = Basket.builder()
                .id(UUID.randomUUID())
                .build();
        basket.setDeletedDate(LocalDateTime.now());

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        given(basketRepository.findById(any())).willReturn(Optional.of(basket));

        //when & then
        Exception exception = assertThrows(NullPointerException.class,
                () -> {
                    assert user != null;
                    basketService.removeProductFromBasket(user.getUsername(), UUID.randomUUID());
                });
        assertEquals(BasketExceptionMessage.BASKET_DELETE.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("장바구니 빼기 실패5 : 장바구니 유저와 다른 경우")
    void removeProductFromBasket_fail5() {
        //given
        User user = User.builder()
                .username("user1")
                .build();
        User user2 = User.builder()
                .username("user2")
                .build();
        Basket basket = Basket.builder()
                .id(UUID.randomUUID())
                .user(user)
                .build();

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user2));
        given(basketRepository.findById(any())).willReturn(Optional.of(basket));

        //when & then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> {
                    assert user2 != null;
                    basketService.removeProductFromBasket(user2.getUsername(), UUID.randomUUID());
                });
        assertEquals(BasketExceptionMessage.BASKET_USER_NOT_EQUALS.getMessage(), exception.getMessage());
    }
}