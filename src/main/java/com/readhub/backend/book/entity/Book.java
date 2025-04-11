package com.readhub.backend.book.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.readhub.backend.bookpage.entity.BookPage;
import com.readhub.backend.global.audit.Auditable;
import com.readhub.backend.rent.entity.Rent;
import com.readhub.backend.rentbook.entity.RentBook;
import com.readhub.backend.reservation.entity.Reservation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
public class Book extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;

    //나중에 카테코리 클래스로 변경
    @Column
    private String category;

    @Column
    private String writer;

    @Column
    private String publisher;

    @Column
    private int rentCount;

    @Enumerated(value = EnumType.STRING)
    private BookStatus bookStatus;

    @Column
    private int likeCount;

    @Column(nullable = false)
    private int viewCount;

    @Column
    private int reviewCount;

    @Column
    private int reservationCount;

    @Column
    private int starPoint;

    @Column
    private String info;

    @Column
    private String writerInfo;

    @Column
    private String keyword;


    public enum BookStatus {

        AVAILABLE(1,"대여 가능"),
        NOT_AVAILABLE(1,"대여  불 가능"),
        BORROWED(2,"대여 중"),
        RESERVED(3,"예약"),
        WAITING(4,"예약 대기");

        @Getter
        public int statusNumber;

        @Getter
        public String statusDescription;

        BookStatus(int statusNumber, String statusDescription){
            this.statusDescription = statusDescription;
            this.statusNumber = statusNumber;
        }
    }

    @JsonBackReference
    @OneToMany(mappedBy = "book",cascade = CascadeType.REMOVE)
    private List<RentBook> rentBookList = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<BookPage> pages = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

}
