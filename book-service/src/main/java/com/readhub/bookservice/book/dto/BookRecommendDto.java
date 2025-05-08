package com.readhub.bookservice.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BookRecommendDto {

    private Long id;
    private String title;
    private String category;
    private String writer;
    private int viewCount;
}
