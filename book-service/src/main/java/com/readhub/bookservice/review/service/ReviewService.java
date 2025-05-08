package com.readhub.bookservice.review.service;

import com.readhub.bookservice.book.entity.Book;
import com.readhub.bookservice.book.repository.BookRepository;
import com.readhub.bookservice.kafka.UserInfoKafkaService;
import com.readhub.global.exception.BusinessLogicException;
import com.readhub.global.exception.ExceptionCode;
import com.readhub.bookservice.review.entity.Review;
import com.readhub.bookservice.review.repository.ReviewRepository;
import com.readhub.global.kafka.UserInfoResponseEvent;
import jakarta.transaction.Transactional;
import lombok.*;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserInfoKafkaService userInfoKafkaService;

    public Review createReview(Review review, Long userId, long bookId) {

        UserInfoResponseEvent user = userInfoKafkaService.fetchUserInfoViaKafka(userId);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BOOK_NOT_FOUND));

        if (reviewRepository.existsByUserIdAndBook(userId, book)) {
            throw new BusinessLogicException(ExceptionCode.REVIEW_EXISTS);
        }

        review.setBook(book);
        review.setUserId(user.getUserId());
        return reviewRepository.save(review);
    }

    public Review updateReview (long id, Long userId,Review review){
        verifyUser(id,userId);
        Review findReview = findReviewByUser(id,userId);

        BeanUtils.copyProperties(review, findReview,"userId","book","createdAt");
        Review updatedReview = reviewRepository.save(findReview);

        return updatedReview;
    }

    public Review findReview(long id){
        Optional<Review> optionalReview = reviewRepository.findById(id);
        Review findReview = optionalReview.orElseThrow(() ->{
            throw new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND);
        });
        verifiedActiveReview(findReview);
        return findReview;
    }

    public Page<Review> findUserReviews(int size, int page, Long userId){
        return reviewRepository.findAllByUserIdAndReviewStatus(userId,
                Review.ReviewStatus.REVIEW_ACTIVE, PageRequest.of(page-1,size, Sort.by("createdAt").descending()));

    }
    public Page<Review> findBookReviews(int size, int page, long bookId){
        return reviewRepository.findAllByBookIdAndReviewStatus(bookId, Review.ReviewStatus.REVIEW_ACTIVE,
                PageRequest.of(page-1, size, Sort.by("id").descending()));
    }

    public Review deleteUserReview(long id, Long userId){

        Review findReview = findReviewByUser(id,userId);
        verifyUser(id, userId);
        findReview.setReviewStatus(Review.ReviewStatus.REVIEW_DELETE);
        Review deletedReview = reviewRepository.save(findReview);

        return deletedReview;
    }

    private void verifiedActiveReview(Review review){
        if(review.getReviewStatus().getNumber() == 2) {

            throw new BusinessLogicException(ExceptionCode.REVIEW_REMOVED);
        }
    }

    public Review findVerifiedReview(long id){
        Optional<Review> optionalReview = reviewRepository.findById(id);
        Review findReview =
                optionalReview.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND));
        return findReview;
    }

    private Review findReviewByUser(long reviewId, Long userId){
        return reviewRepository.findByIdAndUserId(reviewId,userId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND));
    }

    public void verifyUser(long reviewId, long userId){

        Review findReview = findVerifiedReview(reviewId);
        long dbUserId = findReview.getUserId();

        if(userId != dbUserId){
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }
    }
}
