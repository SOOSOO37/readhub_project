package com.readhub.backend.book.repository;

import com.readhub.backend.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
