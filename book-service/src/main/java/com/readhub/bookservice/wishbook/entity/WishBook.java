package com.readhub.bookservice.wishbook.entity;

import com.readhub.global.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class WishBook extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String writer;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WishBook.WishBookStatus wishBookStatus = WishBookStatus.REQUESTED;



    public enum WishBookStatus{

        REQUESTED(1,"희망도서 신청"),

        CANCELLED(2,"희망도서 신청 취소");

        @Getter
        private int number;

        @Getter
        private String description;

        WishBookStatus(int number, String description){
            this.number = number;
            this.description = description;

        }
    }

}
