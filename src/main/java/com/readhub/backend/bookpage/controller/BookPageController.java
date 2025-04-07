package com.readhub.backend.bookpage.controller;

import com.readhub.backend.bookpage.dto.BookPageDto;
import com.readhub.backend.bookpage.entity.BookPage;
import com.readhub.backend.bookpage.mapper.BookPageMapper;
import com.readhub.backend.bookpage.service.BookPageService;
import com.readhub.backend.user.entity.User;
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
                                                       @AuthenticationPrincipal User user) {

            BookPage bookPage = bookPageService.getBookPage(bookId, pageNumber, user.getId());
            return ResponseEntity.ok(mapper.bookPageToBookPageDto(bookPage));
    }
}

