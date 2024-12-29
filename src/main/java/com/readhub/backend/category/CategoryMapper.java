package com.readhub.backend.category;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category categoryPostDtoToCategory (CategoryCreateDto categoryCreateDto);
}
