package com.readhub.backend.review.controller;

import com.readhub.backend.global.response.MultiResponseDto;
import com.readhub.backend.global.utils.UriCreator;
import com.readhub.backend.review.dto.ReviewCreateDto;
import com.readhub.backend.review.dto.ReviewResponseDto;
import com.readhub.backend.review.dto.ReviewUpdateDto;
import com.readhub.backend.review.entity.Review;
import com.readhub.backend.review.mapper.ReviewMapper;
import com.readhub.backend.review.service.ReviewService;
import com.readhub.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/reviews")
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    private final static String REVIEW_DEFAULT_URL = "/reviews";

    private final ReviewMapper mapper;


    @PostMapping
    private ResponseEntity createReview(@RequestBody ReviewCreateDto reviewCreateDto,
                                        @PathVariable Long bookId,
                                        @AuthenticationPrincipal User user) {

        reviewCreateDto.setUserId(user.getId());
        Review review = reviewService.createReview(mapper.reviewCreateDtoToReview(reviewCreateDto),user,bookId);
        URI location = UriCreator.createUri(REVIEW_DEFAULT_URL, review.getId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity updateReview(@PathVariable("id")long id,
                                       @RequestBody ReviewUpdateDto reviewUpdateDto,
                                       @AuthenticationPrincipal User user) {

        reviewUpdateDto.setId(id);
        Review updatedReview = reviewService.updateReview(id,user,mapper.reviewUpdateDtoToReview(reviewUpdateDto));
        ReviewResponseDto reviewResponseDto = mapper.reviewToReviewResponseDto(updatedReview);
        return new ResponseEntity<>(reviewResponseDto, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity findReview(@PathVariable("id") long id){

        Review review = reviewService.findReview(id);
        return new ResponseEntity<>(mapper.reviewToReviewResponseDto(review),HttpStatus.OK);
    }

    @GetMapping("/my-reviews")
    public ResponseEntity findReviewsByUser (@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @AuthenticationPrincipal User user){

        Page<Review> reviewPage = reviewService.findUserReviews(size, page, user);
        List<Review> reviewList = reviewPage.getContent();
        List<ReviewResponseDto> response = mapper.reviewsToReviewResponseDtos(reviewList);
        return new ResponseEntity<>(new MultiResponseDto<>(response,reviewPage),HttpStatus.OK);
    }

    @GetMapping("/book-reviews/{book-id}")
    public ResponseEntity findReviewsByBooks (@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @PathVariable("book-id") long bookId){

        Page<Review> reviewPage = reviewService.findBookReviews(size, page,bookId);
        List<Review> reviewList = reviewPage.getContent();
        List<ReviewResponseDto> response = mapper.reviewsToReviewResponseDtos(reviewList);

        return new ResponseEntity<>(new MultiResponseDto<>(response,reviewPage),HttpStatus.OK);
    }

    @PatchMapping("/removed/{id}")
    public ResponseEntity deleteReviews (@PathVariable long id,
                                         @AuthenticationPrincipal User user){

        Review review = reviewService.deleteUserReview(id,user);
        return new ResponseEntity<>(mapper.reviewToReviewResponseDto(review),HttpStatus.OK);
    }
}
