package com.readhub.backend.book.service;

import com.readhub.backend.book.repository.BookRepository;
import com.readhub.backend.book.entity.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Transactional
@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

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
    
    public Book findBooK (long bookId){
        return findVerifiedBooks(bookId);
    }

    public Book findVerifiedBooks(long bookId) {
        Optional<Book> optionalBooks = bookRepository.findById(bookId);
        Book findBook =
                optionalBooks.orElseThrow(() ->
                        new RuntimeException("Not Found"));
        return findBook;
    }
}




