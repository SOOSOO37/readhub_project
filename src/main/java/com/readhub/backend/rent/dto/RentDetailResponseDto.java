package com.readhub.backend.rent.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class RentDetailResponseDto {

    private long id;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private int extensionCount;
    private String rentStatus;

    private LocalDate createdAt;
    private LocalDate modifiedAt;

    private List<RentedBookDto> rentedBooks;

    @Getter
    @Setter
    public static class RentedBookDto {
        private String title;
    }
}
