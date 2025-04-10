package com.readhub.backend.rentbook.dto;

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
