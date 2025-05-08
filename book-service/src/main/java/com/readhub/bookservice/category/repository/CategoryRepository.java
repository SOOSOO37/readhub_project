package com.readhub.bookservice.category.repository;

import com.readhub.bookservice.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    Category findByName(String name);

    boolean existsByName(String categoryName);
}
