package com.readhub.bookservice.favorite.controller;

import com.readhub.bookservice.favorite.dto.FavoriteResponseDto;
import com.readhub.bookservice.favorite.entity.Favorite;
import com.readhub.bookservice.favorite.mapper.FavoriteMapper;
import com.readhub.bookservice.favorite.service.FavoriteService;
import com.readhub.global.response.MultiResponseDto;
import com.readhub.global.security.userdetail.CustomUserDetails;
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


    @PostMapping("/{bookId}")
    public ResponseEntity favoriteBook(@PathVariable Long bookId,
                                       @AuthenticationPrincipal CustomUserDetails user) {
        Favorite favorite = favoriteService.createFavorite(bookId, user.getId());
        long likeCount = favoriteService.countFavoriteByBookId(bookId);
        boolean isLiked = favoriteService.isBookFavoriteByUser(bookId, user.getId());

        FavoriteResponseDto response = favoriteMapper.favoriteToResponseDto(favorite, likeCount, isLiked);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity cancelFavoriteBook(@PathVariable Long bookId,
                                             @AuthenticationPrincipal CustomUserDetails user) {
        favoriteService.cancelFavorite(bookId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity findAllFavoriteBooks (@RequestParam int page,
                                                @RequestParam int size,
                                                @AuthenticationPrincipal CustomUserDetails user){
        Page<Favorite> favoritePage = favoriteService.findAllFavoriteBooks(page-1, size, user.getId());
        List<Favorite> favoriteList = favoritePage.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(favoriteMapper.favoriteToFavoriteBookResponseDtos(favoriteList),favoritePage), HttpStatus.OK);
    }

}
