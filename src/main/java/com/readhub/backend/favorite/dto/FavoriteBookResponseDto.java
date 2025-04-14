package com.readhub.backend.favorite.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteBookResponseDto {

    private Long favoriteId;
    private Long bookId;
    private String title;
    private String writer;
    private int likeCount;
}
