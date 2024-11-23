package com.sparta.blackwhitedeliverydriver.category.repository;

import com.sparta.blackwhitedeliverydriver.category.entity.Category;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByName(String name);
}
