package com.readhub.bookservice.overdue.entity;

import com.readhub.bookservice.book.entity.Book;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Overdue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(nullable = false)
    private Long userId;

    private String email;

    @Enumerated(value = EnumType.STRING)
    private OverdueStatus overdueStatus;

    private LocalDate overdueDate;

    public enum OverdueStatus {

        ACTIVE(1,"연체중"),
        CANCELED(2,"연체 취소"),
        NOT_RENTABLE(3,"대여 불가");

        @Getter
        public int statusNumber;

        @Getter
        public String statusDescription;

        OverdueStatus(int statusNumber, String statusDescription){
            this.statusDescription = statusDescription;
            this.statusNumber = statusNumber;
        }
    }

}
