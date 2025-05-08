package com.readhub.bookservice.review.entity;

import com.readhub.bookservice.book.entity.Book;
import com.readhub.global.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(nullable = false)
    private Long userId;

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
