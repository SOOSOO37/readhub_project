package com.readhub.bookservice.bookpage.controller;

import com.readhub.bookservice.bookpage.dto.BookPageDto;
import com.readhub.bookservice.bookpage.entity.BookPage;
import com.readhub.bookservice.bookpage.mapper.BookPageMapper;
import com.readhub.bookservice.bookpage.service.BookPageService;
import com.readhub.global.security.userdetail.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/book-page")
@RestController
public class BookPageController {

    private final BookPageService bookPageService;
    private final BookPageMapper mapper;

    @GetMapping("/{bookId}/pages/{pageNumber}")
        public ResponseEntity<BookPageDto> getBookPage(@PathVariable Long bookId,
                                                       @PathVariable int pageNumber,
                                                       @AuthenticationPrincipal CustomUserDetails user) {

            BookPage bookPage = bookPageService.findBookPage(bookId, pageNumber, user.getId());
            return ResponseEntity.ok(mapper.bookPageToBookPageDto(bookPage));
    }
}

