package com.readhub.backend.reservation.entity;

import com.readhub.backend.book.entity.Book;
import com.readhub.backend.global.audit.Auditable;
import com.readhub.backend.rent.entity.Rent;
import com.readhub.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Reservation extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime reservedAt = LocalDateTime.now();

    @Enumerated(value = EnumType.STRING)
    private Reservation.ReservationStatus reservationStatus = ReservationStatus.WAITING;

    public enum ReservationStatus {

        WAITING(1, "예약신청"),
        COMPLETED(2, "예약완료"),
        CANCELLED(3, "예약취소");

        @Getter
        private int number;

        @Getter
        private String description;

        ReservationStatus(int number, String description) {
            this.number = number;
            this.description = description;
        }
    }

}
