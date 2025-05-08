package com.readhub.bookservice.reservation.mapper;

import com.readhub.bookservice.book.entity.Book;
import com.readhub.bookservice.reservation.dto.ReservationCreateDto;
import com.readhub.bookservice.reservation.dto.ReservationResponseDto;
import com.readhub.bookservice.reservation.entity.Reservation;

import org.mapstruct.Mapper;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    default Reservation reservationCreateDtoToReservation(ReservationCreateDto reservationCreateDto) {
        Reservation reservation = new Reservation();
        Book book = new Book();
        book.setId(reservationCreateDto.getBookId());
        reservation.setBook(book);

        BeanUtils.copyProperties(reservationCreateDto, reservation);
        return reservation;
    }

    default ReservationResponseDto reservationToReservationResponseDto(Reservation reservation) {
        ReservationResponseDto response = new ReservationResponseDto();
        BeanUtils.copyProperties(reservation, response);

        response.setTitle(reservation.getBook().getTitle());
        response.setUserId(reservation.getUserId());
        response.setReservationStatus(reservation.getReservationStatus());

        return response;
    }

    List<ReservationResponseDto> reservationsToReservationResponseDtos(List<Reservation> orderList);
}
