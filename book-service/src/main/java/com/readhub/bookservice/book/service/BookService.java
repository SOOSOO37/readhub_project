package com.readhub.bookservice.book.service;

import com.readhub.bookservice.book.repository.BookRepository;
import com.readhub.bookservice.book.entity.Book;
import com.readhub.bookservice.kafka.UserInfoKafkaService;
import com.readhub.global.exception.BusinessLogicException;
import com.readhub.global.exception.ExceptionCode;
import com.readhub.global.kafka.UserInfoResponseEvent;
import com.readhub.global.utils.Sorting;
import com.readhub.bookservice.rent.repository.RentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final Sorting sort;
    private final RentRepository rentRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserInfoKafkaService userInfoKafkaService;

    private static final String RECOMMEND_PREFIX = "recommend::user::";

    public Book createBook(Book book) {
        Book savedBook = bookRepository.save(book);
        return savedBook;
    }

    public Book updateBook(Book book) {
        Book findBook = findVerifiedBooks(book.getId());

        BeanUtils.copyProperties(book,findBook,"id","rentCount", "likeCount", "reviewCount", "reservationCount", "starPoint");
        return bookRepository.save(findBook);
    }

    public Page<Book> getNewBooks (int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return bookRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Book findBook(long id){
        Optional<Book> optionalBook = bookRepository.findById(id);

        if(optionalBook.isPresent()){
            Book book = optionalBook.get();
            book.setViewCount(book.getViewCount()+1);
            this.bookRepository.save(book);
            return book;
        }else {
            throw new BusinessLogicException(ExceptionCode.BOOK_NOT_FOUND);
        }
    }


    public Page<Book> searchBooks(int page, int size, int sorting, String category, String keyword){
        List<Sort.Order> orders = sort.getOrders(sorting);

        if(category.isBlank() && keyword.isBlank()) {
            return bookRepository.findAll(PageRequest.of(page, size, Sort.by(orders)));
        } else if (category.isBlank()) {
            return bookRepository.findByWriterContainingOrTitleContaining(PageRequest.of(page, size, Sort.by(orders)),keyword, keyword);
        }
        return bookRepository.findByCategoryAndKeyword(PageRequest.of(page, size, Sort.by(orders)), category, keyword);
    }

    public void deleteBook(long id){
        Book findBook = findVerifiedBooks(id);

        bookRepository.deleteById(findBook.getId());

    }

    public Book updateBookStatus(long id,Book book){
        Book findBook = findVerifiedBooks(id);

        changeBookStatus(findBook);

        return bookRepository.save(findBook);

    }


    public Page<Book> findRecommendedBooks(Long userId, Pageable pageable) {
        UserInfoResponseEvent userInfo = userInfoKafkaService.fetchUserInfoViaKafka(userId);
            Pageable topTen = PageRequest.of(0, 10);
            Page<Book> recentBooksPage = rentRepository.findRecentRentedBooks(userInfo.getUserId(), topTen);
            List<Book> recentBooks = recentBooksPage.getContent();

            if (!recentBooks.isEmpty()) {
                Book baseBook = recentBooks.get(0);
                return bookRepository.findByWriterOrKeywordWeighted(
                        baseBook.getWriter(), baseBook.getKeyword(), baseBook.getId(), pageable);
            } else {
                return bookRepository.findAllByViewCountDesc(pageable);
            }
    }
    public Page<Book> findBookRanks(int page, int size){

        return bookRepository.findAll(PageRequest.of(page,size, Sort.by("viewCount").descending()));

    }

    public void changeBookStatus(Book book) {

        if (book.getRentCount() == 0) {
            book.setBookStatus(Book.BookStatus.NOT_AVAILABLE);
        }
    }
    public Book findVerifiedBooks(long bookId) {
        Optional<Book> optionalBooks = bookRepository.findById(bookId);
        Book findBook =
                optionalBooks.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.BOOK_NOT_FOUND));
        return findBook;
    }

}




