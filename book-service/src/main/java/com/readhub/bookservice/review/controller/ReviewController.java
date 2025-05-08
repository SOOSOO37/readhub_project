package com.readhub.bookservice.review.controller;

import com.readhub.global.response.MultiResponseDto;
import com.readhub.global.security.userdetail.CustomUserDetails;
import com.readhub.global.utils.UriCreator;
import com.readhub.bookservice.review.dto.ReviewCreateDto;
import com.readhub.bookservice.review.dto.ReviewResponseDto;
import com.readhub.bookservice.review.dto.ReviewUpdateDto;
import com.readhub.bookservice.review.entity.Review;
import com.readhub.bookservice.review.mapper.ReviewMapper;
import com.readhub.bookservice.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
                                        @AuthenticationPrincipal CustomUserDetails user) {

        reviewCreateDto.setUserId(user.getId());
        Review review = reviewService.createReview(mapper.reviewCreateDtoToReview(reviewCreateDto),user.getId(),bookId);
        URI location = UriCreator.createUri(REVIEW_DEFAULT_URL, review.getId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity updateReview(@PathVariable("id")long id,
                                       @RequestBody ReviewUpdateDto reviewUpdateDto,
                                       @AuthenticationPrincipal CustomUserDetails user) {

        reviewUpdateDto.setId(id);
        Review updatedReview = reviewService.updateReview(id,user.getId(),mapper.reviewUpdateDtoToReview(reviewUpdateDto));
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
                                             @AuthenticationPrincipal CustomUserDetails user){

        Page<Review> reviewPage = reviewService.findUserReviews(size, page, user.getId());
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
                                         @AuthenticationPrincipal CustomUserDetails user){

        Review review = reviewService.deleteUserReview(id,user.getId());
        return new ResponseEntity<>(mapper.reviewToReviewResponseDto(review),HttpStatus.OK);
    }
}
