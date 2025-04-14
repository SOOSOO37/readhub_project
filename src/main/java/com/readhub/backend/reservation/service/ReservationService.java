package com.readhub.backend.reservation.service;


import com.readhub.backend.book.entity.Book;
import com.readhub.backend.book.repository.BookRepository;
import com.readhub.backend.global.exception.BusinessLogicException;
import com.readhub.backend.global.exception.ExceptionCode;
import com.readhub.backend.notification.entity.Notification;
import com.readhub.backend.notification.service.NotificationService;
import com.readhub.backend.reservation.entity.Reservation;
import com.readhub.backend.reservation.repository.ReservationRepository;
import com.readhub.backend.user.entity.User;
import com.readhub.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public Reservation createReservation(Reservation reservation, User user) {

        if (user == null) {
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }

        Book book = findVerifiedBook(reservation.getBook().getId());

        if (book.getRentCount() > 0) {
            throw new BusinessLogicException(ExceptionCode.RESERVATION_NOT_ALLOWED);
        }

        reservation.setBook(book);
        reservation.setUser(user);
        reservation.setReservationStatus(Reservation.ReservationStatus.WAITING);
        reservation.setReservedAt(LocalDateTime.now());

        Reservation savedReservation = reservationRepository.save(reservation);
        return savedReservation;
    }

    public Reservation findReservation(Long reservationId, User user) {
        Reservation reservation = findVerifiedReservation(reservationId);
        verifyReservationUser(reservationId, user.getId());
        return reservation;
    }

    public Reservation cancelReservation(Long reservationId, User user) {
        Reservation reservation = findVerifiedReservation(reservationId);
        verifyReservationUser(reservationId, user.getId());
        reservation.setReservationStatus(Reservation.ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        return  reservation;
    }

    public Page<Reservation> findAllReservations(User user, int page, int size) {
        Page<Reservation> reservationPage = reservationRepository.findByUser(user, PageRequest.of(page, size, Sort.by("id").descending()));
        return reservationPage;
    }

    private Book findVerifiedBook(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BOOK_NOT_FOUND));
    }

    private Reservation findVerifiedReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.RESERVATION_NOT_FOUND));
    }

    private void verifyReservationUser(long id, Long userId) {
       Reservation findReservation = findVerifiedReservation(id);
        long dbUserId = findReservation.getUser().getId();

        if(userId != dbUserId){
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }
    }

    public void notifyAvailableToRent(Book book) {
        List<Reservation> waitingList = reservationRepository.findByBookAndReservationStatusOrderByReservedAtAsc(
                book, Reservation.ReservationStatus.WAITING
        );
        if (waitingList.isEmpty()) return;

        Reservation reservation = waitingList.get(0);
        User user = reservation.getUser();

        String content = "'" + book.getTitle() + "' 도서가 대여 가능해졌습니다.";
        String url = "/books/" + book.getId();

        notificationService.send(
                user,
                Notification.NotificationType.AVAILABLE_TO_RENT,
                content,
                url
        );

        reservation.setReservationStatus(Reservation.ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);
    }

}



