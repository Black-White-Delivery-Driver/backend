package com.sparta.blackwhitedeliverydriver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_category")
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "category_id", updatable = false, nullable = false)
    private UUID categoryId;
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    public static Category from(String categoryName) {
        return Category.builder()
                .name(categoryName)
                .build();
    }
}
