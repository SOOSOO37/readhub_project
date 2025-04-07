package com.readhub.backend.rentbook.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RentBookCreateDto {

    private long bookId;

    private int quantity;
}
