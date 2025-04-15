package com.readhub.backend.statistics.mapper;

import com.readhub.backend.book.entity.Book;
import com.readhub.backend.rent.entity.Rent;
import com.readhub.backend.statistics.dto.*;
import org.mapstruct.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring")
public interface StatisticsMapper {

    default RentCountResponseDto rentsToRentCountResponseDto(LocalDate startDate, LocalDate endDate, List<Rent> rents) {
        return new RentCountResponseDto(startDate, endDate, (long) rents. size());
    }

    default RentListResponseDto rentToRentListResponseDto(Rent rent) {
        List<RentListResponseDto.BookInfo> bookInfos = rent.getRentBookList().stream()
                .map(rb -> new RentListResponseDto.BookInfo(
                        rb.getBook().getTitle(),
                        rb.getBook().getWriter(),
                        rb.getQuantity()))
                .toList();

        return new RentListResponseDto(
                rent.getId(),
                rent.getUser().getEmail(),
                rent.getUser().getNickName(),
                rent.getCreatedAt(),
                bookInfos
        );
    }

    default List<RentListResponseDto> rentsToRentListResponseDtos(List<Rent> rents) {
        return rents.stream()
                .map(this::rentToRentListResponseDto)
                .toList();
    }

    default List<MostRentedBookResponseDto> toMostRentedBookDtos(List<Object[]> result) {
        return result.stream()
                .map(obj -> {
                    Book book = (Book) obj[0];
                    Long count = (Long) obj[1];

                    return new MostRentedBookResponseDto(
                            book.getTitle(),
                            book.getWriter(),
                            count
                    );
                })
                .toList();
    }

    default PopularBookResponseDto bookToPopularBookByCategoryDto(Book book) {
        return new PopularBookResponseDto(
                book.getTitle(),
                book.getWriter(),
                book.getViewCount()
        );
    }

    default List<PopularBookResponseDto> booksToPopularBookResponseDtos(List<Book> books) {
        return books.stream()
                .map(this::bookToPopularBookByCategoryDto)
                .toList();
    }
}
