package com.readhub.bookservice.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCreateDto {

    private Long id;
    private Long userId;
    private Long bookId;
    private LocalDateTime reservedAt;

}
