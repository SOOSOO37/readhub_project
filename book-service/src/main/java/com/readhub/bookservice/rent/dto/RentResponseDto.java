package com.readhub.bookservice.rent.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class RentResponseDto {

    private long id;
    private LocalDate createdAt;

    private List<RentDetailResponseDto.RentedBookDto> rentedBooks;

    @Getter
    @Setter
    public static class RentedBookDto {
        private String title;
    }
}

