package com.readhub.backend.review.repository;

import com.readhub.backend.book.entity.Book;
import com.readhub.backend.review.entity.Review;
import com.readhub.backend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review,Long> {

    boolean existsByUserAndBook (User user, Book book);

    Optional<Review> findByIdAndUser(Long id, User user);

    Page<Review> findAllByUserAndReviewStatus(User user, Review.ReviewStatus reviewStatus, Pageable pageable);

    Page<Review> findAllByBookIdAndReviewStatus(long bookId, Review.ReviewStatus reviewStatus, Pageable pageable);
}
