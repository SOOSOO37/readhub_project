package com.readhub.bookservice.rent.dto;

import com.readhub.bookservice.rent.entity.Rent;
import com.readhub.bookservice.rentbook.dto.RentBookCreateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RentCreateDto {

    private long userId;

    private LocalDate dueDate;

    private Rent.RentStatus rentStatus;

    private List<RentBookCreateDto> rentBookDtoList;

}
