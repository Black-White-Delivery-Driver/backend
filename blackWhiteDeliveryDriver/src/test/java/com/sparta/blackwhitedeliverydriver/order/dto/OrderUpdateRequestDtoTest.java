package com.sparta.blackwhitedeliverydriver.order.dto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.sparta.blackwhitedeliverydriver.model.order.entity.OrderStatusEnum;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderUpdateRequestDtoTest {


    private Validator validator;

    @BeforeEach
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("orderId가 null인 경우")
    public void testOrderIdNotNull() {
        OrderUpdateRequestDto request = new OrderUpdateRequestDto(null, OrderStatusEnum.PENDING);

        Set<ConstraintViolation<OrderUpdateRequestDto>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("status가 null인 경우")
    public void testStatusNotNull() {
        OrderUpdateRequestDto request = new OrderUpdateRequestDto(UUID.randomUUID(), null);

        Set<ConstraintViolation<OrderUpdateRequestDto>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("모든 필드가 유효한 경우")
    public void testValidOrderUpdateRequestDto() {
        OrderUpdateRequestDto request = new OrderUpdateRequestDto(UUID.randomUUID(), OrderStatusEnum.ACCEPTED);

        Set<ConstraintViolation<OrderUpdateRequestDto>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

}