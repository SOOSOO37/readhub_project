package com.readhub.bookservice.favorite.repository;

import com.readhub.bookservice.favorite.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository <Favorite, Long> {

    long countByBookId(Long bookId);

    boolean existsByBookIdAndUserId(Long bookId, Long userId);

    Optional<Favorite> findByBookIdAndUserId(Long bookId, Long userId);

    Page<Favorite> findAllByUserId(Long userId, Pageable pageable);
}
