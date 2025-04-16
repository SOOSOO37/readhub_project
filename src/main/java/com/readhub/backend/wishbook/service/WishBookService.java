package com.readhub.backend.wishbook.service;

import com.readhub.backend.global.exception.BusinessLogicException;
import com.readhub.backend.global.exception.ExceptionCode;
import com.readhub.backend.user.entity.User;
import com.readhub.backend.user.repository.UserRepository;
import com.readhub.backend.wishbook.entity.WishBook;
import com.readhub.backend.wishbook.repository.WishBookRepository;
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
    private final UserRepository userRepository;

    public WishBook createWishBook(User user, WishBook wishBook) {
        verifyWishBookUser(wishBook.getId(), user.getId() );

        wishBook.setUser(user);
        return wishBookRepository.save(wishBook);
    }

    public void cancelWishBook(Long id, String email) {
        WishBook wishBook = findVerifiedWishBook(id);

        verifyWishBookUser(wishBook.getId(),wishBook.getUser().getId());

        wishBook.setWishBookStatus(WishBook.WishBookStatus.CANCELLED);
        wishBookRepository.save(wishBook);
    }

    public Page<WishBook> findUserWishBooks( int page, int size,User user) {
        Page<WishBook> wishBookPage = wishBookRepository.findByUser(user,PageRequest.of(page, size, Sort.by("id").descending()));
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
        long dbUserId = findWishBook.getUser().getId();

        if(userId != dbUserId){
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }
    }
}
