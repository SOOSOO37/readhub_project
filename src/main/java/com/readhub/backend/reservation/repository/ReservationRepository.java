package com.readhub.backend.reservation.repository;

import com.readhub.backend.book.entity.Book;
import com.readhub.backend.reservation.entity.Reservation;
import com.readhub.backend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    Page<Reservation> findByUser(User user, Pageable pageable);

    List<Reservation> findByBookAndReservationStatusOrderByReservedAtAsc(
            Book book, Reservation.ReservationStatus reservationStatus);
}
