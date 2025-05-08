package com.readhub.bookservice.reservation.controller;

import com.readhub.global.security.userdetail.CustomUserDetails;
import com.readhub.global.utils.UriCreator;
import com.readhub.bookservice.reservation.dto.ReservationCreateDto;
import com.readhub.bookservice.reservation.entity.Reservation;
import com.readhub.bookservice.reservation.mapper.ReservationMapper;
import com.readhub.bookservice.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

    private final static String RESERVATION_DEFAULT_URL = "/orders";

    private final ReservationMapper mapper;

    private final ReservationService service;

    @PostMapping
    public ResponseEntity createReservation (@RequestBody ReservationCreateDto reservationCreateDto,
                                             @AuthenticationPrincipal CustomUserDetails user){

        reservationCreateDto.setUserId(user.getId());
        Reservation reservation = service.createReservation(mapper.reservationCreateDtoToReservation(reservationCreateDto),user.getId());
        URI location = UriCreator.createUri(RESERVATION_DEFAULT_URL, reservation.getId());

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity findReservation(@PathVariable long id,
                                          @AuthenticationPrincipal CustomUserDetails user) {

        Reservation reservation = service.findReservation(id,user.getId());
        return new ResponseEntity<>(mapper.reservationToReservationResponseDto(reservation), HttpStatus.OK);
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity cancelReservation(@PathVariable long id,
                                            @AuthenticationPrincipal CustomUserDetails user){

        Reservation reservation = service.cancelReservation(id,user.getId());
        return new ResponseEntity<>(mapper.reservationToReservationResponseDto(reservation),HttpStatus.OK);
    }



}
