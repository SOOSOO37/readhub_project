package com.readhub.bookservice.favorite.mapper;

import com.readhub.bookservice.book.entity.Book;
import com.readhub.bookservice.favorite.dto.FavoriteBookResponseDto;
import com.readhub.bookservice.favorite.dto.FavoriteResponseDto;
import com.readhub.bookservice.favorite.entity.Favorite;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {

    default FavoriteResponseDto favoriteToResponseDto(Favorite favorite, long likeCount, boolean isLiked) {
        return new FavoriteResponseDto(
                favorite.getId(),
                favorite.getBook().getId(),
                favorite.getUserId(),
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
