package com.sparta.blackwhitedeliverydriver.store.entity;

import com.sparta.blackwhitedeliverydriver.category.entity.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_store_category")
public class StoreCategory {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "store_category_id", updatable = false, nullable = false)
    private UUID storeCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_category_id", nullable = false)
    private Category category;

    public static StoreCategory from(Store store, Category category) {
        return StoreCategory.builder()
                .store(store)
                .category(category)
                .build();
    }
}
