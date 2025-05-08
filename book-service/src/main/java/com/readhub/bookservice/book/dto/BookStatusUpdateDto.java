package com.readhub.bookservice.book.dto;

import com.readhub.bookservice.book.entity.Book;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookStatusUpdateDto {

    private long id;

    private Book.BookStatus bookStatus;
}
