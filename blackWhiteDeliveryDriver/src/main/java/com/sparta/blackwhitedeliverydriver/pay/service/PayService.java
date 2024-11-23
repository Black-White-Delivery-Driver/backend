package com.sparta.blackwhitedeliverydriver.pay.service;

import com.sparta.blackwhitedeliverydriver.pay.dto.PayApproveResponseDto;
import com.sparta.blackwhitedeliverydriver.pay.dto.PayCancelResponseDto;
import com.sparta.blackwhitedeliverydriver.pay.dto.PayGetDetailResponseDto;
import com.sparta.blackwhitedeliverydriver.pay.dto.PayGetResponseDto;
import com.sparta.blackwhitedeliverydriver.pay.dto.PayReadyResponseDto;
import com.sparta.blackwhitedeliverydriver.pay.dto.PayRefundRequestDto;
import com.sparta.blackwhitedeliverydriver.pay.dto.PayRefundResponseDto;
import com.sparta.blackwhitedeliverydriver.pay.dto.PayRequestDto;
import com.sparta.blackwhitedeliverydriver.order.entity.Order;
import com.sparta.blackwhitedeliverydriver.order.entity.OrderProduct;
import com.sparta.blackwhitedeliverydriver.order.entity.OrderStatusEnum;
import com.sparta.blackwhitedeliverydriver.order.entity.OrderTypeEnum;
import com.sparta.blackwhitedeliverydriver.pay.entity.Pay;
import com.sparta.blackwhitedeliverydriver.pay.entity.PayStatusEnum;
import com.sparta.blackwhitedeliverydriver.user.entity.User;
import com.sparta.blackwhitedeliverydriver.user.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.user.exception.ExceptionMessage;
import com.sparta.blackwhitedeliverydriver.order.exception.OrderExceptionMessage;
import com.sparta.blackwhitedeliverydriver.pay.exception.PayExceptionMessage;
import com.sparta.blackwhitedeliverydriver.order.repository.OrderProductRepository;
import com.sparta.blackwhitedeliverydriver.order.repository.OrderRepository;
import com.sparta.blackwhitedeliverydriver.pay.repository.PayRepository;
import com.sparta.blackwhitedeliverydriver.user.repository.UserRepository;
import com.sparta.blackwhitedeliverydriver.pay.util.HttpUtil;
import com.sparta.blackwhitedeliverydriver.pay.util.PayUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayService {
    private final String PAY_URI = "https://open-api.kakaopay.com/online/v1";

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PayRepository payRepository;
    private final OrderProductRepository orderProductRepository;

    private final PayUtil payUtil;
    private final HttpUtil httpUtil;

    @Transactional
    public PayReadyResponseDto readyToPay(String username, PayRequestDto request) {
        //유저 유효성
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));
        checkDeletedUser(user);

        //주문 유효성
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NullPointerException(OrderExceptionMessage.ORDER_NOT_FOUND.getMessage()));
        checkDeletedOrder(order);

        //유저와 주문 유저 비교
        checkOrderUser(order, user);

        //주문 상태 체크
        checkOrderStatus(order);

        //주문 타입 체크 - 대면인 경우에는 오프라인 계산
        checkOrderType(order);

        //파라미터와 헤더 설정
        Map<String, String> parameters = payUtil.getReadyPayParameters(user, order);
        HttpEntity<Map<String, String>> restRequest = httpUtil.getHttpEntity(payUtil.getHeaders(), parameters);

        //카카오페이에 요청
        RestTemplate restTemplate = new RestTemplate();
        PayReadyResponseDto response = restTemplate.postForObject(PAY_URI + "/payment/ready", restRequest,
                PayReadyResponseDto.class);

        assert response != null;
        order.updateTid(response.getTid());

        return response;
    }

    @Transactional
    public PayApproveResponseDto approvePay(String username, String pgToken, String tid) {

        //유저 유효성
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));
        checkDeletedUser(user);

        //주문 유효성
        Order order = orderRepository.findByTid(tid)
                .orElseThrow(() -> new NullPointerException(OrderExceptionMessage.ORDER_NOT_FOUND.getMessage()));
        checkDeletedOrder(order);

        Map<String, String> parameters = payUtil.getApprovePayParameters(tid, pgToken, order);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, payUtil.getHeaders());

        RestTemplate restTemplate = new RestTemplate();
        PayApproveResponseDto approveResponse = restTemplate.postForObject(
                PAY_URI + "/payment/approve", requestEntity, PayApproveResponseDto.class);

        assert approveResponse != null;
        Pay pay = Pay.of(order, approveResponse);
        payRepository.save(pay);
        order.updateStatus(OrderStatusEnum.PENDING);

        return approveResponse;
    }

    @Transactional
    public PayRefundResponseDto refundPayment(String username, PayRefundRequestDto request) {
        //유저 유효성
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_PUBLIC.getMessage()));
        checkDeletedUser(user);

        //주문 유효성
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NullPointerException(OrderExceptionMessage.ORDER_NOT_FOUND.getMessage()));
        checkDeletedOrder(order);

        //주문 상태 체크
        checkOrderPendingStatus(order);

        //유저와 주문 유저의 유효성
        checkOrderUser(order, user);

        //pay 유효성
        Pay pay = payRepository.findByOrder(order)
                .orElseThrow(() -> new NullPointerException(PayExceptionMessage.PAY_NOT_FOUND.getMessage()));
        checkDeletedPay(pay);

        //Pay 생성 시간이 5분 이내인지 확인
        checkPayWithinFiveMinutes(pay);

        //100% 환불로 일단 구현
        int cancelAmount = pay.getPayAmount();

        //카카오 페이 서버로 보낼 요청 생성 및 api 호출
        Map<String, String> parameters = payUtil.getRefundParameters(pay, cancelAmount);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, payUtil.getHeaders());
        RestTemplate restTemplate = new RestTemplate();
        PayCancelResponseDto cancelResponse = restTemplate.postForObject(PAY_URI + "/payment/cancel", requestEntity,
                PayCancelResponseDto.class);

        //주문 상태 업데이트
        order.updateStatus(OrderStatusEnum.CANCEL);

        //pay 업데이트
        assert cancelResponse != null;
        pay.updateByRefund(PayStatusEnum.REFUND, cancelResponse.getCanceled_amount().getTotal(),
                cancelResponse.getCanceled_at());

        return new PayRefundResponseDto("주문을 취소했습니다.");
    }

    @Transactional
    public void refundPaymentByReject(Order order) {
        //주문 상태 체크
        checkOrderPendingStatus(order);

        //pay 유효성
        Pay pay = payRepository.findByOrder(order)
                .orElseThrow(() -> new NullPointerException(PayExceptionMessage.PAY_NOT_FOUND.getMessage()));
        checkDeletedPay(pay);

        //100% 환불로 일단 구현
        int cancelAmount = pay.getPayAmount();

        //카카오 페이 서버로 보낼 요청 생성 및 api 호출
        Map<String, String> parameters = payUtil.getRefundParameters(pay, cancelAmount);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, payUtil.getHeaders());
        RestTemplate restTemplate = new RestTemplate();
        PayCancelResponseDto cancelResponse = restTemplate.postForObject(PAY_URI + "/payment/cancel", requestEntity,
                PayCancelResponseDto.class);

        //주문 상태 업데이트
        order.updateStatus(OrderStatusEnum.CANCEL);

        //pay 업데이트
        assert cancelResponse != null;
        pay.updateByRefund(PayStatusEnum.REFUND, cancelResponse.getCanceled_amount().getTotal(),
                cancelResponse.getCanceled_at());
    }

    public PayGetDetailResponseDto getPayDetail(String username, UUID payId) {
        //유저 유효성
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NullPointerException(ExceptionMessage.USER_NOT_FOUND.getMessage()));
        checkDeletedUser(user);

        //PAY 유효성
        Pay pay = payRepository.findById(payId)
                .orElseThrow(() -> new NullPointerException(PayExceptionMessage.PAY_NOT_FOUND.getMessage()));
        checkDeletedPay(pay);

        //Order와 유저 유효성
        UserRoleEnum role = user.getRole();
        if (role.equals(UserRoleEnum.CUSTOMER)) {
            checkOrderUser(pay.getOrder(), user);
        }

        //Order에 관한 OrderProduct 조회
        List<OrderProduct> orderProducts = orderProductRepository.findAllByOrderAndNotDeleted(pay.getOrder());

        return PayGetDetailResponseDto.ofPayAndOrderProducts(pay, orderProducts);
    }

    public Page<PayGetResponseDto> getPays(String username, int page, int size, String sortBy, boolean isAsc) {
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

        //유저 권한별 반환
        Page<Pay> pays;
        UserRoleEnum role = user.getRole();
        if (role.equals(UserRoleEnum.CUSTOMER)) {
            pays = payRepository.findAllByUser(user, pageable);
        } else {
            pays = payRepository.findAll(pageable);
        }

        return pays.map(PayGetResponseDto::fromPay);
    }

    public Page<PayGetResponseDto> searchPaymentsByStoreName(String storeName, int page, int size, String sortBy, boolean isAsc) {
        // 정렬 및 페이징 정보 생성
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // storeName으로 Pay 검색
        Page<Pay> payments = payRepository.findByStoreNameContaining(storeName, pageable);

        // Pay 데이터를 DTO로 변환하여 반환
        return payments.map(PayGetResponseDto::fromPay);
    }

    private void checkOrderUser(Order order, User user) {
        String orderUsername = order.getUser().getUsername();
        String username = user.getUsername();
        if (!orderUsername.equals(username)) {
            throw new IllegalArgumentException(OrderExceptionMessage.ORDER_USER_NOT_EQUALS.getMessage());
        }
    }

    private void checkOrderStatus(Order order) {
        if (!order.getStatus().equals(OrderStatusEnum.CREATE)) {
            throw new IllegalArgumentException(OrderExceptionMessage.ORDER_UNABLE_PAY_STATUS.getMessage());
        }
    }

    private void checkDeletedUser(User user) {
        if (user.getDeletedDate() != null || user.getDeletedBy() != null) {
            throw new IllegalArgumentException(ExceptionMessage.USER_DELETED.getMessage());
        }
    }

    private void checkDeletedOrder(Order order) {
        if (order.getDeletedDate() != null || order.getDeletedBy() != null) {
            throw new IllegalArgumentException(OrderExceptionMessage.ORDER_NOT_FOUND.getMessage());
        }
    }

    private void checkDeletedPay(Pay pay) {
        if (pay.getDeletedDate() != null || pay.getDeletedBy() != null) {
            throw new IllegalArgumentException(PayExceptionMessage.PAY_NOT_FOUND.getMessage());
        }
    }

    private void checkOrderType(Order order) {
        if (order.getType().equals(OrderTypeEnum.OFFLINE)) {
            throw new IllegalArgumentException(PayExceptionMessage.PAY_OFFLINE_TYPE.getMessage());
        }
    }

    private void checkOrderPendingStatus(Order order) {
        if (!order.getStatus().equals(OrderStatusEnum.PENDING)) {
            throw new IllegalArgumentException(PayExceptionMessage.PAY_UNABLE.getMessage());
        }
    }

    private void checkPayWithinFiveMinutes(Pay pay) {
        if (pay.getApprovedAt() == null) {
            throw new IllegalArgumentException(PayExceptionMessage.PAY_UNABLE.getMessage());
        }

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(pay.getApprovedAt(), now);

        // 5분 초과 시 예외 발생
        if (duration.toMinutes() > 5) {
            throw new IllegalArgumentException(PayExceptionMessage.PAY_REFUND_TIME_EXCEEDED.getMessage());
        }
    }
}
