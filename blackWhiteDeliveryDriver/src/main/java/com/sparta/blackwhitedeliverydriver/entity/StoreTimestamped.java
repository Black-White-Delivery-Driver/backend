package com.sparta.blackwhitedeliverydriver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class StoreTimestamped {

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    //@Column(updatable = false, nullable = false)
    @Column(updatable = false)
    private String createdBy = "홍길동";

    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column
    private String updatedBy;

    @Column
    private LocalDateTime deletedAt;

    @Column
    private String deletedBy;

    public void markDeleted(String deletedBy) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }
}
