package com.readhub.backend.review.service;

import com.readhub.backend.book.entity.Book;
import com.readhub.backend.book.repository.BookRepository;
import com.readhub.backend.global.exception.BusinessLogicException;
import com.readhub.backend.global.exception.ExceptionCode;
import com.readhub.backend.review.entity.Review;
import com.readhub.backend.review.repository.ReviewRepository;
import com.readhub.backend.user.entity.User;
import com.readhub.backend.user.repository.UserRepository;
import com.readhub.backend.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.*;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final UserService userService;

    private final BookRepository bookRepository;

    public Review createReview(Review review, User user, long bookId) {

        if (user != null) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BOOK_NOT_FOUND));

            if (reviewRepository.existsByUserAndBook(review.getUser(), book)) {
                throw new BusinessLogicException(ExceptionCode.REVIEW_EXISTS);
            }
            review.setBook(book);
            Review savedReview = reviewRepository.save(review);
            return savedReview;
        }
        throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
    }

    public Review updateReview (long id, User user,Review review){
        verifyUser(id,user.getId());
        Review findReview = findReviewByUser(id,user);

        BeanUtils.copyProperties(review, findReview,"user","book","createdAt");
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

    public Page<Review> findUserReviews(int size, int page, User user){
        User findUser = userService.findVerifiedUser(user.getId());
        return reviewRepository.findAllByUserAndReviewStatus(findUser,
                Review.ReviewStatus.REVIEW_ACTIVE, PageRequest.of(page-1,size, Sort.by("createdAt").descending()));

    }
    public Page<Review> findBookReviews(int size, int page, long bookId){
        return reviewRepository.findAllByBookIdAndReviewStatus(bookId, Review.ReviewStatus.REVIEW_ACTIVE,
                PageRequest.of(page-1, size, Sort.by("id").descending()));
    }

    public Review deleteUserReview(long id, User user){

        Review findReview = findReviewByUser(id,user);
        verifyUser(id, user.getId());
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

    private Review findReviewByUser(long reviewId, User user){
        return reviewRepository.findByIdAndUser(reviewId,user)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND));
    }

    public void verifyUser(long reviewId, long userId){

        Review findReview = findVerifiedReview(reviewId);
        long dbUserId = findReview.getUser().getId();

        if(userId != dbUserId){
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }
    }
}
