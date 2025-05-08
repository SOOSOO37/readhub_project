package com.readhub.bookservice.rentbook.dto;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RentBookExtendResponseDto {
    private long rentId;
    private LocalDate returnDate;
    private int extensionCount;
    private String email;
}
