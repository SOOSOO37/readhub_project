package com.readhub.backend.favorite.controller;

import com.readhub.backend.book.entity.Book;
import com.readhub.backend.book.service.BookService;
import com.readhub.backend.favorite.dto.FavoriteResponseDto;
import com.readhub.backend.favorite.entity.Favorite;
import com.readhub.backend.favorite.mapper.FavoriteMapper;
import com.readhub.backend.favorite.service.FavoriteService;
import com.readhub.backend.global.response.MultiResponseDto;
import com.readhub.backend.user.entity.User;
import com.readhub.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final FavoriteMapper favoriteMapper;


    @PostMapping("/{bookId}/")
    public ResponseEntity favoriteBook(@PathVariable Long bookId,
                                       @AuthenticationPrincipal User user) {
        Favorite favorite = favoriteService.createFavorite(bookId, user);
        long likeCount = favoriteService.countFavoriteByBookId(bookId);
        boolean isLiked = favoriteService.isBookFavoriteByUser(bookId, user.getId());

        FavoriteResponseDto response = favoriteMapper.favoriteToResponseDto(favorite, likeCount, isLiked);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity cancelFavoriteBook(@PathVariable Long bookId,
                                         @AuthenticationPrincipal User user) {
        favoriteService.cancelFavorite(bookId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity findAllFavoriteBooks (@RequestParam int page,
                                                @RequestParam int size,
                                                @AuthenticationPrincipal User user){
        Page<Favorite> favoritePage = favoriteService.findAllFavoriteBooks(page-1, size,user);
        List<Favorite> favoriteList = favoritePage.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(favoriteMapper.favoriteToFavoriteBookResponseDtos(favoriteList),favoritePage), HttpStatus.OK);
    }

}
