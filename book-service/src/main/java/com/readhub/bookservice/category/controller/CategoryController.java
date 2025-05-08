package com.readhub.bookservice.category.controller;

import com.readhub.bookservice.category.entity.Category;
import com.readhub.bookservice.category.dto.CategoryCreateDto;
import com.readhub.bookservice.category.mapper.CategoryMapper;
import com.readhub.bookservice.category.service.CategoryService;
import com.readhub.global.utils.UriCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    private final CategoryMapper mapper;

    private final static String CATEGORY_DEFAULT_URL = "/categories";

    @PostMapping
    public ResponseEntity createCategory(@RequestBody CategoryCreateDto categoryCreateDto){

        Category category = categoryService.createCategory(mapper.categoryPostDtoToCategory(categoryCreateDto));
        URI location = UriCreator.createUri(CATEGORY_DEFAULT_URL,category.getId());

        return ResponseEntity.created(location).build();

    }

    @GetMapping("/all")
    public ResponseEntity findAllCategories(@RequestParam int page,
                                            @RequestParam int size){

        Page<Category> categories = categoryService.findAllCategory(page -1, size);
        List<Category> productList = categories.getContent();

        return new ResponseEntity<>(productList, HttpStatus.OK);
    }
}
