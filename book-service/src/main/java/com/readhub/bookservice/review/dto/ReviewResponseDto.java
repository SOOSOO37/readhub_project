package com.readhub.bookservice.review.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewResponseDto {
    private long id;

    private String title;

    private String content;

    private int score;
}
