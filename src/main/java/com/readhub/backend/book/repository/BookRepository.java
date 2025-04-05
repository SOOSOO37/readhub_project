package com.readhub.backend.book.repository;

import com.readhub.backend.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Book> findByWriterContainingOrTitleContaining(Pageable pageable, String keyword1, String keyword2);

    @Query("select p from Book p where p.category=:category and (p.writer like %:keyword% or p.title like %:keyword%)")
    Page<Book> findByCategoryAndKeyword(Pageable pageable, String category, String keyword);

}
