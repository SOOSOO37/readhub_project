package com.readhub.bookservice.favorite.service;


import com.readhub.bookservice.book.entity.Book;
import com.readhub.bookservice.book.repository.BookRepository;
import com.readhub.bookservice.favorite.entity.Favorite;
import com.readhub.bookservice.favorite.repository.FavoriteRepository;
import com.readhub.bookservice.kafka.UserInfoKafkaService;
import com.readhub.global.exception.BusinessLogicException;
import com.readhub.global.exception.ExceptionCode;
import com.readhub.global.kafka.UserInfoResponseEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Transactional
@RequiredArgsConstructor
@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final BookRepository bookRepository;
    private final UserInfoKafkaService userInfoKafkaService;



    public Favorite createFavorite(Long bookId, Long userId) {
        Book findBook = findVerifiedBook(bookId);
        validateAlreadyFavorite(bookId, userId);
        UserInfoResponseEvent user = userInfoKafkaService.fetchUserInfoViaKafka(userId);

        Favorite favorite = new Favorite();
        favorite.setBook(findBook);
        favorite.setUserEmail(user.getEmail());
        favorite.setUserNickname(user.getNickname());

        findBook.increaseLikeCount();

        return favoriteRepository.save(favorite);
    }

    public void cancelFavorite(Long bookId, Long userId) {
        UserInfoResponseEvent user = userInfoKafkaService.fetchUserInfoViaKafka(userId);
        Favorite findFavorite = VerifiedFavoriteUser(bookId, user.getUserId());
        favoriteRepository.delete(findFavorite);

        Book book = findFavorite.getBook();
        book.decreaseLikeCount();
    }

    public boolean isBookFavoriteByUser(Long bookId, Long userId) {
        return favoriteRepository.existsByBookIdAndUserId(bookId, userId);
    }

    private void validateAlreadyFavorite(Long bookId, Long userId) {
        if (isBookFavoriteByUser(bookId, userId)) {
            throw new BusinessLogicException(ExceptionCode.ALREADY_FAVORITE);
        }
    }


    public Book findVerifiedBook (long id){
        Optional<Book> findBook = bookRepository.findById(id);
        Book book = findBook.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.BOOK_NOT_FOUND));
        return book;
    }

    private Favorite VerifiedFavoriteUser (Long bookId, Long userId) {
        return favoriteRepository.findByBookIdAndUserId(bookId, userId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.FAVORITE_NOT_FOUND));
    }

    public long countFavoriteByBookId(Long bookId) {
        return favoriteRepository.countByBookId(bookId);
    }

    public Page<Favorite> findAllFavoriteBooks (int page, int size, Long userId){
        UserInfoResponseEvent user = userInfoKafkaService.fetchUserInfoViaKafka(userId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return favoriteRepository.findAllByUserId(user.getUserId(), pageable);

    }


}
