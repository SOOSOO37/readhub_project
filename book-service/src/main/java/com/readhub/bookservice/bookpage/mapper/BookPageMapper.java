package com.readhub.bookservice.bookpage.mapper;


import com.readhub.bookservice.bookpage.dto.BookPageDto;
import com.readhub.bookservice.bookpage.entity.BookPage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookPageMapper {

    BookPageDto bookPageToBookPageDto(BookPage bookPage);
}
