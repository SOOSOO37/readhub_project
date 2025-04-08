package com.readhub.backend.reservation.mapper;

import com.readhub.backend.book.entity.Book;
import com.readhub.backend.reservation.dto.ReservationCreateDto;
import com.readhub.backend.reservation.dto.ReservationResponseDto;
import com.readhub.backend.reservation.entity.Reservation;
import com.readhub.backend.user.entity.User;
import org.mapstruct.Mapper;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    default Reservation reservationCreateDtoToReservation(ReservationCreateDto reservationCreateDto) {
        Reservation reservation = new Reservation();
        BeanUtils.copyProperties(reservationCreateDto, reservation);
        return reservation;
    }

    default ReservationResponseDto reservationToReservationResponseDto(Reservation reservation) {
        ReservationResponseDto response = new ReservationResponseDto();
        BeanUtils.copyProperties(reservation, response);

        response.setTitle(reservation.getBook().getTitle());
        response.setEmail(reservation.getUser().getEmail());
        response.setReservationStatus(reservation.getReservationStatus());

        return response;
    }

    List<ReservationResponseDto> reservationsToReservationResponseDtos(List<Reservation> orderList);
}
