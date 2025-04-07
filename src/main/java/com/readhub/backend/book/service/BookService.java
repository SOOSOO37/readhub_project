package com.readhub.backend.book.service;

import com.readhub.backend.book.repository.BookRepository;
import com.readhub.backend.book.entity.Book;
import com.readhub.backend.global.exception.BusinessLogicException;
import com.readhub.backend.global.exception.ExceptionCode;
import com.readhub.backend.global.utils.Sorting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final Sorting sort;

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

    //
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




