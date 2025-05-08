package com.readhub.bookservice.rentbook.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RentBookResponseDto {
    private String title;
    private String writer;
    private String nickName;
    private String email;
}
