package com.readhub.bookservice.category.mapper;

import com.readhub.bookservice.category.dto.CategoryCreateDto;
import com.readhub.bookservice.category.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category categoryPostDtoToCategory (CategoryCreateDto categoryCreateDto);
}
