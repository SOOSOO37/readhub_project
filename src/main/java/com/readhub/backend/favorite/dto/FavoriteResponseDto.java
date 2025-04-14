package com.readhub.backend.favorite.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteResponseDto {

    private long favoriteId;
    private long bookId;
    private long userId;
    private long likeCount;
    private boolean isLiked;
}
