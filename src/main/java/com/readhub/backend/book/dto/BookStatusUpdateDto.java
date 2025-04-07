package com.readhub.backend.book.dto;

import com.readhub.backend.book.entity.Book;
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
