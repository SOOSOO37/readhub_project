package com.readhub.backend.book;

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
