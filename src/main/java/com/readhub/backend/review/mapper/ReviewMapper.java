package com.readhub.backend.review.mapper;

import com.readhub.backend.book.entity.Book;
import com.readhub.backend.global.exception.BusinessLogicException;
import com.readhub.backend.global.exception.ExceptionCode;
import com.readhub.backend.review.dto.ReviewCreateDto;
import com.readhub.backend.review.dto.ReviewResponseDto;
import com.readhub.backend.review.dto.ReviewUpdateDto;
import com.readhub.backend.review.entity.Review;
import org.mapstruct.Mapper;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    default Review reviewCreateDtoToReview (ReviewCreateDto reviewCreateDto){
        Book book = new Book();
        Review review = new Review();

        review.setBook(book);
        BeanUtils.copyProperties(reviewCreateDto, review);

        return review;
    }

    Review reviewUpdateDtoToReview(ReviewUpdateDto reviewUpdateDto);

    default ReviewResponseDto reviewToReviewResponseDto (Review review){

        ReviewResponseDto response = new ReviewResponseDto();

            BeanUtils.copyProperties(review, response);

            return response;
    }

    default List<ReviewResponseDto> reviewsToReviewResponseDtos(List<Review> reviews) {
        if (reviews == null) {
            throw new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND);
        }
        List<ReviewResponseDto> response = new ArrayList<ReviewResponseDto>(reviews.size());
        for (Review review : reviews) {
            response.add(reviewToReviewResponseDto(review));
        }
        return response;
    }


}
