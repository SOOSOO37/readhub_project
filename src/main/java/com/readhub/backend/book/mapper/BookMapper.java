package com.readhub.backend.book.mapper;

import com.readhub.backend.book.dto.BookCreateDto;
import com.readhub.backend.book.dto.BookDetailResponseDto;
import com.readhub.backend.book.dto.BookResponseDto;
import com.readhub.backend.book.dto.BookUpdateDto;
import com.readhub.backend.book.entity.Book;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {

    Book bookCreateDtoToBook (BookCreateDto bookCreateDto);
    Book bookUpdateDtoToBook (BookUpdateDto bookUpdateDto);
    BookResponseDto bookToBookResponseDto(Book book);
    BookDetailResponseDto bookToBookDetailResponseDto(Book book);
    List<BookResponseDto> booksToBookResponseDtos(List<Book> bookList);


}
