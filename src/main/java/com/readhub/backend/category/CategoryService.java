package com.readhub.backend.category;

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
            throw new RuntimeException("이미 존재하는 카테고리 입니다");
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
