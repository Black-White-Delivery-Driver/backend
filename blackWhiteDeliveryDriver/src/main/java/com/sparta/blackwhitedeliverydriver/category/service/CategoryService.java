package com.sparta.blackwhitedeliverydriver.category.service;

import com.sparta.blackwhitedeliverydriver.category.dto.CategoryIdResponseDto;
import com.sparta.blackwhitedeliverydriver.category.dto.CategoryRequestDto;
import com.sparta.blackwhitedeliverydriver.category.dto.CategoryResponseDto;
import com.sparta.blackwhitedeliverydriver.category.entity.Category;
import com.sparta.blackwhitedeliverydriver.user.entity.User;
import com.sparta.blackwhitedeliverydriver.category.exception.CategoryExceptionMessage;
import com.sparta.blackwhitedeliverydriver.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public List<Category> getOrCreateCategory(String categoryNames, User user) {
        Set<String> categorySet = new HashSet<>();
        Arrays.stream(categoryNames.split(","))
                .map(String::trim)
                .forEach(categorySet::add);

        List<Category> categoryList = new ArrayList<>();
        for(String categoryName : categorySet) {
            Category category = categoryRepository.findByName(categoryName)
                    .orElseGet(() -> {
                        // 없으면 새로 생성하고 저장 -> 예외 처리하기
                        Category newCategory = Category.from(categoryName);
                        categoryList.add(newCategory);
                        return categoryRepository.save(newCategory);
                    });
            categoryList.add(category);
        }

        return categoryList;
    }

    public Page<CategoryResponseDto> getAllCategories(int page, int size, String sortBy, boolean isAsc) {
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        // 페이징 처리
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        return categoryPage.map(CategoryResponseDto::from);
    }

    @Transactional
    public CategoryIdResponseDto createCategory(CategoryRequestDto requestDto) {
        checkCategoryName(requestDto.getName(), null);

        Category category = Category.from(requestDto.getName());
        categoryRepository.save(category);

        return new CategoryIdResponseDto(category.getCategoryId());
    }

    @Transactional
    public CategoryIdResponseDto updateCategory(CategoryRequestDto requestDto, UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(CategoryExceptionMessage.CATEGORY_NOT_FOUND.getMessage()));

        checkCategoryName(requestDto.getName(), category.getName());

        category.update(requestDto.getName());
        categoryRepository.save(category);

        return new CategoryIdResponseDto(category.getCategoryId());
    }

    @Transactional
    public CategoryIdResponseDto deleteCategory(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(CategoryExceptionMessage.CATEGORY_NOT_FOUND.getMessage()));

        categoryRepository.delete(category);

        return new CategoryIdResponseDto(category.getCategoryId());
    }

    private void checkCategoryName(String name, String currentName) {
        categoryRepository.findByName(name)
                .filter(category -> !category.getName().equals(currentName))
                .ifPresent(category -> {
                    throw new IllegalArgumentException(CategoryExceptionMessage.CATEGORY_DUPLICATED.getMessage());
                });
    }
}
