package com.readhub.backend.reservation.controller;

import com.readhub.backend.global.response.MultiResponseDto;
import com.readhub.backend.global.utils.UriCreator;
import com.readhub.backend.reservation.dto.ReservationCreateDto;
import com.readhub.backend.reservation.entity.Reservation;
import com.readhub.backend.reservation.mapper.ReservationMapper;
import com.readhub.backend.reservation.service.ReservationService;
import com.readhub.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

    private final static String RESERVATION_DEFAULT_URL = "/orders";

    private final ReservationMapper mapper;

    private final ReservationService service;

    @PostMapping
    public ResponseEntity createReservation (@RequestBody ReservationCreateDto reservationCreateDto,
                                             @AuthenticationPrincipal User user){

        reservationCreateDto.setUserId(user.getId());
        Reservation reservation = service.createReservation(mapper.reservationCreateDtoToReservation(reservationCreateDto),user);
        URI location = UriCreator.createUri(RESERVATION_DEFAULT_URL, reservation.getId());

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity findReservation(@PathVariable long id,
                                          @AuthenticationPrincipal User user) {

        Reservation reservation = service.findReservation(id,user);
        return new ResponseEntity<>(mapper.reservationToReservationResponseDto(reservation), HttpStatus.OK);
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity cancelReservation(@PathVariable long id,
                                            @AuthenticationPrincipal User user){

        Reservation reservation = service.cancelReservation(id,user);
        return new ResponseEntity<>(mapper.reservationToReservationResponseDto(reservation),HttpStatus.OK);
    }



}
