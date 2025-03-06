package com.readhub.backend.category.mapper;

import com.readhub.backend.category.dto.CategoryCreateDto;
import com.readhub.backend.category.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category categoryPostDtoToCategory (CategoryCreateDto categoryCreateDto);
}
