package com.readhub.bookservice.category.service;

import com.readhub.bookservice.category.entity.Category;
import com.readhub.bookservice.category.repository.CategoryRepository;
import com.readhub.global.exception.BusinessLogicException;
import com.readhub.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(Category category){
        if (isCategoryNameExists(category.getName())) {
            throw new BusinessLogicException(ExceptionCode.CATEGORY_EXISTS);
        }
        Category savedCategory = categoryRepository.save(category);
        return savedCategory;
    }

    public Page<Category> findAllCategory(int page, int size){
        Page<Category> categoryPage = categoryRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
        return categoryPage;
    }

    public Category findCategory (String name){
        return categoryRepository.findByName(name);
    }

    private boolean isCategoryNameExists(String categoryName) {
        return categoryRepository.existsByName(categoryName);
    }
}
