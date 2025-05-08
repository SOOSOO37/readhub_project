package com.readhub.bookservice.reservation.repository;

import com.readhub.bookservice.book.entity.Book;
import com.readhub.bookservice.reservation.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    Page<Reservation> findByUserId(Long userId, Pageable pageable);

    List<Reservation> findByBookAndReservationStatusOrderByReservedAtAsc(
            Book book, Reservation.ReservationStatus reservationStatus);
}
