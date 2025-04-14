package com.readhub.backend.favorite.service;


import com.readhub.backend.book.entity.Book;
import com.readhub.backend.book.repository.BookRepository;
import com.readhub.backend.favorite.entity.Favorite;
import com.readhub.backend.favorite.repository.FavoriteRepository;
import com.readhub.backend.global.exception.BusinessLogicException;
import com.readhub.backend.global.exception.ExceptionCode;
import com.readhub.backend.user.entity.User;
import com.readhub.backend.user.repository.UserRepository;
import com.readhub.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final UserRepository userRepository;
    private final UserService userService;


    public Favorite createFavorite(Long bookId, User user) {
        Book findBook = findVerifiedBook(bookId);
        validateAlreadyFavorite(bookId, user.getId());

        Favorite favorite = new Favorite();
        favorite.setBook(findBook);
        favorite.setUser(user);

        findBook.increaseLikeCount();

        return favoriteRepository.save(favorite);
    }

    public void cancelFavorite(Long bookId, User user) {
        Favorite findFavorite = VerifiedFavoriteUser(bookId, user.getId());

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

    public Page<Favorite> findAllFavoriteBooks (int page, int size, User user){
        Page<Favorite> favoritePage = favoriteRepository.findAllByUser(user, PageRequest.of(page, size, Sort.by("id").descending()));

        return favoritePage;

    }


}
