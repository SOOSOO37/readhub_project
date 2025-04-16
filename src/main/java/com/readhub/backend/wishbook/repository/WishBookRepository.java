package com.readhub.backend.wishbook.repository;

import com.readhub.backend.user.entity.User;
import com.readhub.backend.wishbook.entity.WishBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishBookRepository extends JpaRepository<WishBook, Long> {

    Page<WishBook> findByUser(User user, Pageable pageable);
}
