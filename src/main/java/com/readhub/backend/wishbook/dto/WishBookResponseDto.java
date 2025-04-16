package com.readhub.backend.wishbook.dto;

import com.readhub.backend.wishbook.entity.WishBook;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishBookResponseDto {

    private Long id;
    private String title;
    private String writer;
    private WishBook.WishBookStatus wishBookStatus;
    private String email;
}
