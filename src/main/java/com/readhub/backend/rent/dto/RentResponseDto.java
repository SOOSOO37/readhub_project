package com.readhub.backend.rent.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RentResponseDto {

    private long id;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private int extensionCount;
    private String rentStatus;

    private LocalDate createdAt;
    private LocalDate modifiedAt;
}
