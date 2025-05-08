package com.readhub.bookservice.bookpage.repository;

import com.readhub.bookservice.bookpage.entity.BookPage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookPageRepository extends JpaRepository<BookPage,Long> {
    Optional<BookPage> findByBookIdAndPageNumber(Long bookId, int pageNumber);
}
