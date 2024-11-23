package com.sparta.blackwhitedeliverydriver.bascket.dto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BasketUpdateRequestDtoTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("basketId가 null인 경우")
    public void testProductIdNotNull() {
        BasketUpdateRequestDto request = BasketUpdateRequestDto.builder()
                .basketId(null)
                .quantity(10)
                .build();

        Set<ConstraintViolation<BasketUpdateRequestDto>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("quantity가 null인 경우")
    public void testQuantityNotNull() {
        BasketUpdateRequestDto request = BasketUpdateRequestDto.builder()
                .basketId(UUID.randomUUID())
                .quantity(null)
                .build();

        Set<ConstraintViolation<BasketUpdateRequestDto>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("quantity가 1인 경우 (경계값)")
    public void testQuantityMinBoundary() {
        BasketUpdateRequestDto request = BasketUpdateRequestDto.builder()
                .basketId(UUID.randomUUID())
                .quantity(1)
                .build();

        Set<ConstraintViolation<BasketUpdateRequestDto>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("quantity가 99인 경우 (경계값)")
    public void testQuantityMaxBoundary() {
        BasketUpdateRequestDto request = BasketUpdateRequestDto.builder()
                .basketId(UUID.randomUUID())
                .quantity(99)
                .build();

        Set<ConstraintViolation<BasketUpdateRequestDto>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("quantity가 0인 경우 (하한 경계 밖)")
    public void testQuantityBelowMin() {
        BasketUpdateRequestDto request = BasketUpdateRequestDto.builder()
                .basketId(UUID.randomUUID())
                .quantity(0)
                .build();

        Set<ConstraintViolation<BasketUpdateRequestDto>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("quantity가 100인 경우 (상한 경계 밖)")
    public void testQuantityAboveMax() {
        BasketUpdateRequestDto request = BasketUpdateRequestDto.builder()
                .basketId(UUID.randomUUID())
                .quantity(100)
                .build();

        Set<ConstraintViolation<BasketUpdateRequestDto>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
    }
}