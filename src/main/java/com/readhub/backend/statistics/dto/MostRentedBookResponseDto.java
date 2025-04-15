package com.readhub.backend.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MostRentedBookResponseDto {

    private String title;
    private String writer;
    private Long rentCount;
}
