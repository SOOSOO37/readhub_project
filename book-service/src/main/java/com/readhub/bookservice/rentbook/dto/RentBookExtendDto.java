package com.readhub.bookservice.rentbook.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RentBookExtendDto {

    private Long rentBookId;
    private Long userId;
}
