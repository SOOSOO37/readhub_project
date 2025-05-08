package com.readhub.bookservice.book.dto;

import com.readhub.bookservice.book.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookUpdateDto {

    private long id;

    private String title;

    private String category;

    private String writer;

    private String publisher;

    private Book.BookStatus bookStatus;

    private String info;

    private String writerInfo;
}
