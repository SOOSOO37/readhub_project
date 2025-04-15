package com.readhub.backend.book.repository;

import com.readhub.backend.book.entity.Book;
import io.lettuce.core.dynamic.annotation.Param;
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

    Page<Book> findByCategoryOrderByViewCountDesc(String category, Pageable pageable);

    Page<Book> findAllByOrderByViewCountDesc(Pageable pageable);

    @Query("SELECT b FROM Book b " +
            "WHERE b.id <> :excludeId AND " +
            "(LOWER(b.writer) = LOWER(:writer) OR LOWER(b.keyword) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY " +
            "CASE " +
            "WHEN LOWER(b.writer) = LOWER(:writer) AND LOWER(b.keyword) LIKE LOWER(CONCAT('%', :keyword, '%')) THEN 1 " +
            "WHEN LOWER(b.writer) = LOWER(:writer) THEN 2 " +
            "WHEN LOWER(b.keyword) LIKE LOWER(CONCAT('%', :keyword, '%')) THEN 3 " +
            "ELSE 4 END, " +
            "b.viewCount DESC")
    Page<Book> findByWriterOrKeywordWeighted(@Param("writer") String writer,
                                             @Param("keyword") String keyword,
                                             @Param("excludeId") Long excludeId,
                                             Pageable pageable);

    @Query("SELECT b FROM Book b ORDER BY b.viewCount DESC")
    Page<Book> findAllByViewCountDesc(Pageable pageable);




}
