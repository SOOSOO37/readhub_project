package com.readhub.backend.bookpage.service;

import com.readhub.backend.bookpage.entity.BookPage;
import com.readhub.backend.bookpage.repository.BookPageRepository;
import com.readhub.backend.global.exception.BusinessLogicException;
import com.readhub.backend.global.exception.ExceptionCode;
import com.readhub.backend.rent.repository.RentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookPageService {

    private final BookPageRepository bookPageRepository;
    private final RentRepository rentRepository;

    public BookPage getBookPage(Long bookId, int pageNumber, Long userId) {

        verifyUserRentedBook(userId,bookId);

        return bookPageRepository.findByBookIdAndPageNumber(bookId, pageNumber)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PAGE_NOT_FOUND));
    }

    private void verifyUserRentedBook(Long userId, Long bookId) {
        boolean rented = rentRepository.existsByUserIdAndRentBookListBookId(userId, bookId);
        if (!rented) {
            throw new BusinessLogicException(ExceptionCode.PERMISSION_NOT_EXIST);
        }
    }
}
