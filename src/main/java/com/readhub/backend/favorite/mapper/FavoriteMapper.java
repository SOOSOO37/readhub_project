package com.readhub.backend.favorite.mapper;

import com.readhub.backend.book.entity.Book;
import com.readhub.backend.favorite.dto.FavoriteBookResponseDto;
import com.readhub.backend.favorite.dto.FavoriteResponseDto;
import com.readhub.backend.favorite.entity.Favorite;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {

    default FavoriteResponseDto favoriteToResponseDto(Favorite favorite, long likeCount, boolean isLiked) {
        return new FavoriteResponseDto(
                favorite.getId(),
                favorite.getBook().getId(),
                favorite.getUser().getId(),
                likeCount,
                isLiked
        );
    }
    default FavoriteBookResponseDto favoriteToFavoriteBookResponseDto(Favorite favorite) {
        Book book = favorite.getBook();
        return new FavoriteBookResponseDto(
                favorite.getId(),
                book.getId(),
                book.getTitle(),
                book.getWriter(),
                book.getLikeCount()
        );
    }
    List<FavoriteBookResponseDto> favoriteToFavoriteBookResponseDtos (List<Favorite> favoriteList);
}
