package com.readhub.backend.rent.dto;

import com.readhub.backend.rent.entity.Rent;
import com.readhub.backend.rentbook.dto.RentBookCreateDto;
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
