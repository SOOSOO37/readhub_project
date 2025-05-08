package com.readhub.bookservice.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateDto {

    private long userId;

    private long bookId;

    private String title;

    private String content;

    private int score;
}
