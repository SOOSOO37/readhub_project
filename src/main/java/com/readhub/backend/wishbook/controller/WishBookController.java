package com.readhub.backend.wishbook.controller;

import com.readhub.backend.global.response.MultiResponseDto;
import com.readhub.backend.global.utils.UriCreator;
import com.readhub.backend.user.entity.User;
import com.readhub.backend.wishbook.dto.WishBookCreateDto;
import com.readhub.backend.wishbook.entity.WishBook;
import com.readhub.backend.wishbook.mapper.WishBookMapper;
import com.readhub.backend.wishbook.service.WishBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/wish-books")
@RestController
public class WishBookController {

    private final WishBookService wishBookService;
    private final WishBookMapper mapper;

    private final static String WISH_BOOK_DEFAULT_URL = "/wish-books";

    @PostMapping
    public ResponseEntity createWishBook(@RequestBody WishBookCreateDto wishBookCreateDto,
                                         @AuthenticationPrincipal User user) {
        WishBook wishBook = wishBookService.createWishBook(user, mapper.wishBookCreateDtoToWishBook(wishBookCreateDto));
        URI location = UriCreator.createUri(WISH_BOOK_DEFAULT_URL, wishBook.getId());
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity cancelWishBook(@PathVariable("id") Long id,
                                            @AuthenticationPrincipal User user) {
        wishBookService.cancelWishBook(id, user.getEmail());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/my")
    public ResponseEntity getUserWishBooks(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @AuthenticationPrincipal User user) {
        Page<WishBook> wishBookPage = wishBookService.findUserWishBooks( page-1, size,user);
        List<WishBook> wishBookList = wishBookPage.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.wishBooksToWishBookResponseDtos(wishBookList), wishBookPage), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getAllWishBooks(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        Page<WishBook> wishBookPage = wishBookService.findAllWishBooks(page, size);
        List<WishBook> wishBookList = wishBookPage.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.wishBooksToWishBookResponseDtos(wishBookList), wishBookPage), HttpStatus.OK);
    }
}
