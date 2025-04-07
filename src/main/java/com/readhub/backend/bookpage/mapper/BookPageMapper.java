package com.readhub.backend.bookpage.mapper;

import com.readhub.backend.book.dto.BookCreateDto;
import com.readhub.backend.book.dto.BookDetailResponseDto;
import com.readhub.backend.book.entity.Book;
import com.readhub.backend.bookpage.dto.BookPageDto;
import com.readhub.backend.bookpage.entity.BookPage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookPageMapper {

    BookPageDto bookPageToBookPageDto(BookPage bookPage);
}
