package com.sparta.blackwhitedeliverydriver.category.controller;

import com.sparta.blackwhitedeliverydriver.category.dto.CategoryIdResponseDto;
import com.sparta.blackwhitedeliverydriver.category.dto.CategoryRequestDto;
import com.sparta.blackwhitedeliverydriver.category.dto.CategoryResponseDto;
import com.sparta.blackwhitedeliverydriver.category.service.CategoryService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/")
    public ResponseEntity<Page<CategoryResponseDto>> getAllCategories(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc) {

        Page<CategoryResponseDto> categoryResponseDtos = categoryService.getAllCategories(page-1, size, sortBy, isAsc);

        return ResponseEntity.status(HttpStatus.OK).body(categoryResponseDtos);
    }

    @Secured({"ROLE_MANAGER", "ROLE_MASTER"})
    @PostMapping("/")
    public ResponseEntity<CategoryIdResponseDto> createCategory(@RequestBody CategoryRequestDto requestDto) {
        CategoryIdResponseDto responseDto = categoryService.createCategory(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Secured({"ROLE_MANAGER", "ROLE_MASTER"})
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryIdResponseDto> updateCategory(@RequestBody CategoryRequestDto requestDto, @PathVariable UUID categoryId) {
        CategoryIdResponseDto responseDto = categoryService.updateCategory(requestDto, categoryId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Secured({"ROLE_MANAGER", "ROLE_MASTER"})
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<CategoryIdResponseDto> deleteCategory(@PathVariable UUID categoryId) {
        CategoryIdResponseDto responseDto = categoryService.deleteCategory(categoryId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
