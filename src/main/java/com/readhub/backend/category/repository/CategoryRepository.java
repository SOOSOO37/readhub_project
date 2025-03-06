package com.readhub.backend.category.repository;

import com.readhub.backend.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    Category findByName(String name);

    boolean existsByName(String categoryName);
}
