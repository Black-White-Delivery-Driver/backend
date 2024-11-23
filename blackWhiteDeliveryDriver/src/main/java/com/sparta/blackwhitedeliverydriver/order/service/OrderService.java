package com.sparta.blackwhitedeliverydriver.order.service;

import com.sparta.blackwhitedeliverydriver.order.dto.OrderAddRequestDto;
import com.sparta.blackwhitedeliverydriver.order.dto.OrderGetDetailResponseDto;
import com.sparta.blackwhitedeliverydriver.order.dto.OrderGetResponseDto;
import com.sparta.blackwhitedeliverydriver.order.dto.OrderResponseDto;
import com.sparta.blackwhitedeliverydriver.order.dto.OrderUpdateRequestDto;
import com.sparta.blackwhitedeliverydriver.bascket.entity.Basket;
import com.sparta.blackwhitedeliverydriver.order.entity.Order;
import com.sparta.blackwhitedeliverydriver.order.entity.OrderProduct;
import com.sparta.blackwhitedeliverydriver.order.entity.OrderStatusEnum;
import com.sparta.blackwhitedeliverydriver.order.entity.OrderTypeEnum;
import com.sparta.blackwhitedeliverydriver.product.entity.Product;
import com.sparta.blackwhitedeliverydriver.pay.service.PayService;
import com.sparta.blackwhitedeliverydriver.store.entity.Store;
import com.sparta.blackwhitedeliverydriver.user.entity.User;
import com.sparta.blackwhitedeliverydriver.user.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.bascket.exception.BasketExceptionMessage;
import com.sparta.blackwhitedeliverydriver.user.exception.ExceptionMessage;
import com.sparta.blackwhitedeliverydriver.order.exception.OrderExceptionMessage;
import com.sparta.blackwhitedeliverydriver.store.exception.StoreExceptionMessage;
import com.sparta.blackwhitedeliverydriver.bascket.repository.BasketRepository;
import com.sparta.blackwhitedeliverydriver.order.repository.OrderProductRepository;
import com.sparta.blackwhitedeliverydriver.order.repository.OrderRepository;
import com.sparta.blackwhitedeliverydriver.store.repository.StoreRepository;
import com.sparta.blackwhitedeliverydriver.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class OrderService {

    private final BasketRepository basketRepository;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    private final PayService payService;

    @Transactional
    public OrderResponseDto createOrder(String username, OrderAddRequestDto request) {
        //유저 유효성 검사
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));
        checkDeletedUser(user);

        //이미 존재하는 주문 건이 있는지 확인
        checkCreatedOrderByUser(user);

        //유저와 관련된 장바구니 품목 찾기
        List<Basket> baskets = basketRepository.findAllByUserAndNotDeleted(user);

        //장바구니 개수 체크
        checkBasketCount(baskets);

        //order 엔티티 생성 및 저장
        Store store = baskets.get(0).getStore();
        Order order = Order.ofUserAndStore(user, store, request.getType());
        order = orderRepository.save(order);

        //연관관계 테이블에 장바구니 품목 저장
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (Basket basket : baskets) {
            Product product = basket.getProduct();
            OrderProduct orderProduct = OrderProduct.of(basket, product, order);
            orderProducts.add(orderProduct);
        }
        orderProductRepository.saveAll(orderProducts);

        //장바구니 삭제
        for (Basket basket : baskets) {
            basket.softDelete(username, LocalDateTime.now());
            basketRepository.save(basket);
        }

        //최종금액 계산 및 업데이트
        int price = calculateFinalPay(orderProducts);
        order.updateFinalPay(price);
        orderRepository.save(order);

        return new OrderResponseDto(order.getId());
    }

    public OrderGetDetailResponseDto getOrderDetail(String username, UUID orderId) {
        //유저 유효성
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));
        checkDeletedUser(user);

        //주문 유효성
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NullPointerException(OrderExceptionMessage.ORDER_NOT_FOUND.getMessage()));
        checkDeletedOrder(order);

        // CUSTOMER인 경우 Order의 user인지 체크
        if (user.getRole().equals(UserRoleEnum.CUSTOMER)) {
            checkOrderUser(order, user);
        }

        // Product Entity 구현되면 음식 목록도 포함하여 리턴
        List<OrderProduct> orderProducts = orderProductRepository.findAllByOrderAndNotDeleted(order);

        return OrderGetDetailResponseDto.of(order, orderProducts);
    }

    public Page<OrderGetResponseDto> getOrders(String username, int page, int size, String sortBy, boolean isAsc) {
        //유저 유효성
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));
        checkDeletedUser(user);

        //페이징
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        //주문 조회
        UserRoleEnum role = user.getRole();
        Page<Order> orders;
        if (role.equals(UserRoleEnum.CUSTOMER)) {
            orders = orderRepository.findAllByUserAndNotDeleted(user, pageable);
        } else {
            orders = orderRepository.findAll(pageable);
        }

        return orders.map(OrderGetResponseDto::fromOrder);
    }

    public Page<OrderGetResponseDto> getOrdersByStore(String username, int page, int size, String sortBy, boolean isAsc,
                                                      UUID storeId) {
        //유저 유효성
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));
        checkDeletedUser(user);

        //점포 유효성
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new NullPointerException(
                StoreExceptionMessage.STORE_NOT_FOUND.getMessage()));
        checkDeletedStore(store);

        //유저 점포 유효성
        UserRoleEnum role = user.getRole();
        if (role.equals(UserRoleEnum.OWNER)) {
            checkStoreOwnerEquals(store, user);
        }

        //페이징
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        //주문 조회
        Page<Order> orders = orderRepository.findAllByStoreAndNotDeleted(store, pageable);

        return orders.map(OrderGetResponseDto::fromOrder);
    }

    public Page<OrderGetResponseDto> searchOrdersByStoreName(String storeName, int page, int size, String sortBy,
                                                             boolean isAsc) {
        // 페이징 및 정렬 정보 생성
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // 점포 이름으로 주문 검색
        Page<Order> orders = orderRepository.findByStoreNameContaining(storeName, pageable);

        // DTO로 변환하여 반환
        return orders.map(OrderGetResponseDto::fromOrder);
    }

    @Transactional
    public OrderResponseDto updateOrderStatus(String username, OrderUpdateRequestDto request) {
        //유저 유효성
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));
        checkDeletedUser(user);

        //주문 유효성
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NullPointerException(OrderExceptionMessage.ORDER_NOT_FOUND.getMessage()));
        checkDeletedOrder(order);

        //주문의 점포 주인과 유저 체크
        checkStoreOwnerEquals(order.getStore(), user);

        //점포 주인이 거절하면 환불
        if (request.getStatus().equals(OrderStatusEnum.REJECTED)) {
            checkOrderStatus(order, OrderStatusEnum.PENDING);
            payService.refundPaymentByReject(order);
        } else if (request.getStatus().equals(OrderStatusEnum.ACCEPTED)) {
            checkOrderStatus(order, OrderStatusEnum.PENDING);
        } else if (request.getStatus().equals(OrderStatusEnum.COMPLETED)) {
            if (order.getType().equals(OrderTypeEnum.ONLINE)) {
                checkOrderStatus(order, OrderStatusEnum.ACCEPTED);
            }
        } else {
            throw new IllegalArgumentException(OrderExceptionMessage.ORDER_UNABLE_UPDATE.getMessage());
        }

        order.updateStatus(request.getStatus());
        return new OrderResponseDto(order.getId());
    }

    @Transactional
    public OrderResponseDto deleteOrder(String username, UUID orderId) {
        //유저 유효성
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));
        checkDeletedUser(user);

        //주문 유효성
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NullPointerException(OrderExceptionMessage.ORDER_NOT_FOUND.getMessage()));
        checkDeletedOrder(order);

        //주문 유저 와 API 호출한 유저 체크
        checkOrderUser(order, user);

        //주문 상태 확인
        checkEnableDeleteOrderStatus(order);

        //orderProduct 조회 후 basket 저장
        List<OrderProduct> orderProducts = orderProductRepository.findAllByOrderAndNotDeleted(order);
        for (OrderProduct orderProduct : orderProducts) {
            Basket basket = Basket.ofUserAndOrderProduct(user, orderProduct.getProduct(), orderProduct);
            basketRepository.save(basket);
        }

        // OrderProduct 소프트 딜리트 처리
        for (OrderProduct orderProduct : orderProducts) {
            orderProduct.softDelete(user.getUsername(), LocalDateTime.now());
            orderProductRepository.save(orderProduct);
        }

        //order 삭제
        order.softDelete(username, LocalDateTime.now());
        orderRepository.save(order);

        return new OrderResponseDto(order.getId());
    }

    private void checkBasketCount(List<Basket> baskets) {
        if (baskets.isEmpty()) {
            throw new IllegalArgumentException(BasketExceptionMessage.BASKET_COUNT_ZERO.getMessage());
        }
    }

    private int calculateFinalPay(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .mapToInt(orderProduct -> orderProduct.getPrice() * orderProduct.getQuantity())
                .sum();
    }

    private void checkOrderUser(Order order, User user) {
        String orderUsername = order.getUser().getUsername();
        String username = user.getUsername();
        if (!orderUsername.equals(username)) {
            throw new IllegalArgumentException(OrderExceptionMessage.ORDER_USER_NOT_EQUALS.getMessage());
        }
    }

    private void checkEnableDeleteOrderStatus(Order order) {
        if (!order.getStatus().equals(OrderStatusEnum.CREATE) && !order.getStatus().equals(OrderStatusEnum.PENDING)) {
            throw new IllegalArgumentException(OrderExceptionMessage.ORDER_UNABLE_DELETE_STATUS.getMessage());
        }
    }

    private void checkStoreOwnerEquals(Store store, User user) {
        User owner = store.getUser();
        if (!owner.getUsername().equals(user.getUsername())) {
            throw new IllegalArgumentException("점포 오너 권한이 없습니다.");
        }
    }

    private void checkDeletedUser(User user) {
        if (user.getDeletedDate() != null || user.getDeletedBy() != null) {
            throw new IllegalArgumentException(ExceptionMessage.USER_DELETED.getMessage());
        }
    }

    private void checkDeletedStore(Store store) {
        if (store.getDeletedDate() != null || store.getDeletedBy() != null) {
            throw new IllegalArgumentException(StoreExceptionMessage.STORE_NOT_FOUND.getMessage());
        }
    }

    private void checkDeletedOrder(Order order) {
        if (order.getDeletedDate() != null || order.getDeletedBy() != null) {
            throw new IllegalArgumentException(OrderExceptionMessage.ORDER_NOT_FOUND.getMessage());
        }
    }

    private void checkOrderStatus(Order order, OrderStatusEnum status) {
        if (!order.getStatus().equals(status)) {
            throw new IllegalArgumentException(OrderExceptionMessage.ORDER_UNABLE_UPDATE.getMessage());
        }
    }

    private void checkCreatedOrderByUser(User user) {
        orderRepository.findActiveOrderByUser(user)
                .ifPresent(order -> {
                    throw new IllegalArgumentException(OrderExceptionMessage.ORDER_ALREADY_EXIST.getMessage());
                });
    }
}
