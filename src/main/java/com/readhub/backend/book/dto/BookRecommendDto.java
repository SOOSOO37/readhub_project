package com.readhub.backend.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookRecommendDto {

    private Long id;
    private String title;
    private String category;
    private String writer;
    private int viewCount;
}
