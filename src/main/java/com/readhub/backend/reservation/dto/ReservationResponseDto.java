package com.readhub.backend.reservation.dto;

import com.readhub.backend.reservation.entity.Reservation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationResponseDto {
    private Long id;
    private String title;
    private String email;
    private LocalDateTime reservedAt;
    private Reservation.ReservationStatus reservationStatus;
}
