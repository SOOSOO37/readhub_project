package com.readhub.bookservice.wishbook.repository;

import com.readhub.bookservice.wishbook.entity.WishBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishBookRepository extends JpaRepository<WishBook, Long> {

    Page<WishBook> findByUserId(Long userId, Pageable pageable);
}
