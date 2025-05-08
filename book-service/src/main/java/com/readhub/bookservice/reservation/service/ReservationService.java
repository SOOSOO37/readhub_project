package com.readhub.bookservice.reservation.service;


import com.readhub.bookservice.book.entity.Book;
import com.readhub.bookservice.book.repository.BookRepository;
import com.readhub.bookservice.kafka.UserInfoCache;
import com.readhub.bookservice.kafka.UserInfoKafkaService;
import com.readhub.global.exception.BusinessLogicException;
import com.readhub.global.exception.ExceptionCode;
import com.readhub.bookservice.notification.entity.Notification;
import com.readhub.bookservice.notification.service.NotificationService;
import com.readhub.global.kafka.UserInfoRequestEvent;
import com.readhub.global.kafka.UserInfoResponseEvent;
import com.readhub.bookservice.reservation.entity.Reservation;
import com.readhub.bookservice.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;
    private final NotificationService notificationService;
    private final UserInfoKafkaService userInfoKafkaService;

    public Reservation createReservation(Reservation reservation, Long userId) {

        Book book = findVerifiedBook(reservation.getBook().getId());

        if (book.getRentCount() > 0) {
            throw new BusinessLogicException(ExceptionCode.RESERVATION_NOT_ALLOWED);
        }

        UserInfoResponseEvent userInfo = userInfoKafkaService.fetchUserInfoViaKafka(userId);

        reservation.setBook(book);
        reservation.setUserId(userInfo.getUserId());
        reservation.setReservationStatus(Reservation.ReservationStatus.WAITING);
        reservation.setReservedAt(LocalDateTime.now());

        return reservationRepository.save(reservation);

    }

    public Reservation findReservation (Long reservationId, Long userId){

        Reservation reservation = findVerifiedReservation(reservationId);
        verifyReservationUser(reservation, userId);
        return reservation;
    }

    public Reservation cancelReservation (Long reservationId, Long userId){
        Reservation reservation = findVerifiedReservation(reservationId);
        verifyReservationUser(reservation, userId);
        reservation.setReservationStatus(Reservation.ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        return reservation;
    }

    public Page<Reservation> findAllReservations (Long userId,int page, int size){
        Page<Reservation> reservationPage = reservationRepository.findByUserId(userId, PageRequest.of(page, size, Sort.by("id").descending()));
        return reservationPage;
    }

    private Book findVerifiedBook (Long bookId){
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BOOK_NOT_FOUND));
    }

    private Reservation findVerifiedReservation (Long reservationId){
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.RESERVATION_NOT_FOUND));
    }

    private void verifyReservationUser(Reservation reservation, Long userId) {
        if (!reservation.getUserId().equals(userId)) {
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }
    }

    public void notifyAvailableToRent (Book book){
        List<Reservation> waitingList = reservationRepository.findByBookAndReservationStatusOrderByReservedAtAsc(
                book, Reservation.ReservationStatus.WAITING
        );
        if (waitingList.isEmpty()) return;

        Reservation reservation = waitingList.get(0);
        Long userId = reservation.getUserId();

        UserInfoResponseEvent user = userInfoKafkaService.fetchUserInfoViaKafka(userId);
        String content = "'" + book.getTitle() + "' 도서가 대여 가능해졌습니다.";
        String url = "/books/" + book.getId();

        notificationService.send(
                user.getUserId(),
                Notification.NotificationType.AVAILABLE_TO_RENT,
                content,
                url
        );

        reservation.setReservationStatus(Reservation.ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);
    }
}



