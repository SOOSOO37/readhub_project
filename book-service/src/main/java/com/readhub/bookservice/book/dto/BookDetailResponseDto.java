package com.readhub.bookservice.book.dto;

import com.readhub.bookservice.book.entity.Book;
import com.readhub.global.audit.Auditable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDetailResponseDto extends Auditable {

    private long id;

    private String title;

    private String category;

    private String writer;

    private String publisher;

    private int rentCount;

    private Book.BookStatus bookStatus;

    private int viewCount;

    private int likeCount;

    private int reviewCount;

    private int reservationCount;

    private int starPoint;

    private String info;

    private String writerInfo;
}
