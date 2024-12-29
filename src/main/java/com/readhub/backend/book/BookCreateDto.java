package com.readhub.backend.book;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookCreateDto {

    private long id;

    private String title;

    private String category;

    private String writer;

    private String publisher;

    private int rentCount;

    private Book.BookStatus bookStatus;

    private int likeCount;

    private int reviewCount;

    private int reservationCount;

    private int starPoint;

    private String info;

    private String writerInfo;
}
