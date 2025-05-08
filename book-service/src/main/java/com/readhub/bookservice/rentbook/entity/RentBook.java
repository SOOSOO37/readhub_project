package com.readhub.bookservice.rentbook.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.readhub.bookservice.book.entity.Book;
import com.readhub.global.audit.Auditable;
import com.readhub.bookservice.rent.entity.Rent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class RentBook extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long rentBookId;

    @ManyToOne
    @JoinColumn(name = "rent_id")
    private Rent rent;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "book_id")
    @JsonManagedReference
    private Book book;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentBookStatus rentBookStatus = RentBookStatus.RENT_STANDBY;



    public enum RentBookStatus{

        RENT_STANDBY(1,"대출대기"),

        RENT_FINISH(2,"대출완료");

        @Getter
        private int number;

        @Getter
        private String description;

        RentBookStatus(int number, String description){
            this.number = number;
            this.description = description;

        }
    }
}
