package com.readhub.bookservice.bookpage.service;

import com.readhub.bookservice.bookpage.entity.BookPage;
import com.readhub.bookservice.bookpage.repository.BookPageRepository;
import com.readhub.bookservice.kafka.UserInfoKafkaService;
import com.readhub.global.exception.BusinessLogicException;
import com.readhub.global.exception.ExceptionCode;
import com.readhub.bookservice.rent.repository.RentRepository;
import com.readhub.global.kafka.UserInfoResponseEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookPageService {

    private final BookPageRepository bookPageRepository;
    private final RentRepository rentRepository;
    private final UserInfoKafkaService userInfoKafkaService;

    public BookPage findBookPage(Long bookId, int pageNumber, Long userId) {

        verifyUserRentedBook(userId,bookId);

        return bookPageRepository.findByBookIdAndPageNumber(bookId, pageNumber)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PAGE_NOT_FOUND));
    }

    private void verifyUserRentedBook(Long userId, Long bookId) {
        UserInfoResponseEvent user = userInfoKafkaService.fetchUserInfoViaKafka(userId);
        boolean rented = rentRepository.existsByUserIdAndRentBookListBookId(user.getUserId(), bookId);
        if (!rented) {
            throw new BusinessLogicException(ExceptionCode.PERMISSION_NOT_EXIST);
        }
    }
}
