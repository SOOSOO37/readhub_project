package com.readhub.bookservice.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class RentCountResponseDto {

    private LocalDate startDate;
    private LocalDate endDate;
    private Long count;
}
