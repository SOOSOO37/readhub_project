package com.readhub.backend.rent.entity;

import com.readhub.backend.book.entity.Book;
import com.readhub.backend.global.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Rent extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(name = "extension_count", nullable = false)
    private int extensionCount = 1;

    @Enumerated(value = EnumType.STRING)
    private RentStatus rentStatus = RentStatus.RENT;

    public enum RentStatus {

        RENT(1, "대출중"),
        RETURNED(2, "반납완료"),
        OVERDUE(3, "연체");

        @Getter
        private int number;

        @Getter
        private String description;

        RentStatus(int number, String description) {
            this.number = number;
            this.description = description;
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}
