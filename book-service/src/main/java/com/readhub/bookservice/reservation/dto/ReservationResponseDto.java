package com.readhub.bookservice.reservation.dto;

import com.readhub.bookservice.reservation.entity.Reservation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationResponseDto {
    private Long id;
    private Long userId;
    private String title;
    private LocalDateTime reservedAt;
    private Reservation.ReservationStatus reservationStatus;
}
