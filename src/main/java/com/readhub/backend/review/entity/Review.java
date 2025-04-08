package com.readhub.backend.review.entity;

import com.readhub.backend.book.entity.Book;
import com.readhub.backend.global.audit.Auditable;
import com.readhub.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class Review extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;

    @Lob
    private String content;

    @Column(nullable = false)
    private int score;

    @Enumerated(value = EnumType.STRING)
    private ReviewStatus reviewStatus = ReviewStatus.REVIEW_ACTIVE;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;


    public enum ReviewStatus{

        REVIEW_ACTIVE(1, "작성된 리뷰"),
        REVIEW_DELETE(2, "삭제된 리뷰");

        @Getter
        private int number;
        @Getter
        private String description;

        ReviewStatus(int number, String description) {
            this.number = number;
            this.description = description;
        }
    }
}
