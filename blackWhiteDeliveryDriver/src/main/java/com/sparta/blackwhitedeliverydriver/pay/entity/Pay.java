package com.sparta.blackwhitedeliverydriver.pay.entity;

import com.sparta.blackwhitedeliverydriver.common.entity.BaseEntity;
import com.sparta.blackwhitedeliverydriver.order.entity.Order;
import com.sparta.blackwhitedeliverydriver.pay.dto.PayApproveResponseDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_pay")
public class Pay extends BaseEntity {
    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private String tid;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PayStatusEnum payStatus;

    @Column(nullable = false)
    private Integer payAmount;

    private Integer refundAmount;

    private LocalDateTime approvedAt;
    private LocalDateTime canceledAt;

    public static Pay of(Order order, PayApproveResponseDto approveResponse) {
        return Pay.builder()
                .order(order)
                .payStatus(PayStatusEnum.SUCCESS)
                .itemName(approveResponse.getItem_name())
                .tid(approveResponse.getTid())
                .payAmount(approveResponse.getAmount().getTotal())
                .approvedAt(LocalDateTime.parse(approveResponse.getApproved_at()))
                .build();
    }

    public void updateByRefund(PayStatusEnum payStatusEnum, int total, String canceledAt) {
        this.payStatus = payStatusEnum;
        this.refundAmount = total;
        this.canceledAt = LocalDateTime.parse(canceledAt);
    }
}
