package com.readhub.backend.book.mapper;

import com.readhub.backend.book.dto.*;
import com.readhub.backend.book.entity.Book;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BookMapper {

    Book bookCreateDtoToBook (BookCreateDto bookCreateDto);
    Book bookUpdateDtoToBook (BookUpdateDto bookUpdateDto);
    BookResponseDto bookToBookResponseDto(Book book);
    BookDetailResponseDto bookToBookDetailResponseDto(Book book);
    Book bookStatusPatchDtoToBook(BookStatusUpdateDto bookStatusUpdateDto);

    default List<BookResponseDto> booksToBookResponseDtos(List<Book> books){
        List<BookResponseDto> responses = books.stream()
                .map(this::bookToBookResponseDto)
                .collect(Collectors.toList());
        return responses;
    }


}
