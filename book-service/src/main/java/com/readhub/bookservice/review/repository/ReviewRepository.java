package com.readhub.bookservice.review.repository;

import com.readhub.bookservice.book.entity.Book;
import com.readhub.bookservice.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review,Long> {

    boolean existsByUserIdAndBook(Long userId, Book book);

    Optional<Review> findByIdAndUserId(Long id, Long userId);

    Page<Review> findAllByUserIdAndReviewStatus(Long userId, Review.ReviewStatus status, Pageable pageable);

    Page<Review> findAllByBookIdAndReviewStatus(long bookId, Review.ReviewStatus reviewStatus, Pageable pageable);

}
