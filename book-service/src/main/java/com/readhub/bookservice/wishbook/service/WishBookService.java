package com.readhub.bookservice.wishbook.service;

import com.readhub.bookservice.kafka.UserInfoKafkaService;
import com.readhub.global.exception.BusinessLogicException;
import com.readhub.global.exception.ExceptionCode;
import com.readhub.bookservice.wishbook.entity.WishBook;
import com.readhub.bookservice.wishbook.repository.WishBookRepository;
import com.readhub.global.kafka.UserInfoResponseEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Transactional
@Service
public class WishBookService {

    private final WishBookRepository wishBookRepository;
    private final UserInfoKafkaService userInfoKafkaService;

    public WishBook createWishBook(Long userId, WishBook wishBook) {
        verifyWishBookUser(wishBook.getId(), userId );

        UserInfoResponseEvent user = userInfoKafkaService.fetchUserInfoViaKafka(userId);
        wishBook.setUserId(user.getUserId());

        return wishBookRepository.save(wishBook);
    }

    public void cancelWishBook(Long id, Long userId) {
        WishBook wishBook = findVerifiedWishBook(id);
        verifyWishBookUser(wishBook.getId(),userId);

        wishBook.setWishBookStatus(WishBook.WishBookStatus.CANCELLED);
        wishBookRepository.save(wishBook);
    }

    public Page<WishBook> findUserWishBooks( int page, int size,Long userId) {
        Page<WishBook> wishBookPage = wishBookRepository.findByUserId(userId,PageRequest.of(page, size, Sort.by("id").descending()));
        return wishBookPage;
    }

    public Page<WishBook> findAllWishBooks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return wishBookRepository.findAll(pageable);
    }

    public WishBook findVerifiedWishBook(Long id) {
        return wishBookRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BOOK_NOT_FOUND));
    }

    public void verifyWishBookUser(long wishBookId, long userId){
        WishBook findWishBook = findVerifiedWishBook(wishBookId);
        long dbUserId = findWishBook.getUserId();

        if(userId != dbUserId){
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }
    }
}
