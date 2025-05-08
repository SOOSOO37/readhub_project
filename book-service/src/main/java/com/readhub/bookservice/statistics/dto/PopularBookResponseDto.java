package com.readhub.bookservice.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PopularBookResponseDto {

    private String title;
    private String writer;
    private int viewCount;
}
