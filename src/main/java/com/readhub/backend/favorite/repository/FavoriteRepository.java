package com.readhub.backend.favorite.repository;

import com.readhub.backend.favorite.entity.Favorite;
import com.readhub.backend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository <Favorite, Long> {

    long countByBookId(Long bookId);

    boolean existsByBookIdAndUserId(Long bookId, Long userId);

    Optional<Favorite> findByBookIdAndUserId(Long bookId, Long userId);

    Page<Favorite> findAllByUser(User user, Pageable pageable);
}
