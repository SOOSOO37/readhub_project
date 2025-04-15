package com.readhub.backend.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentListResponseDto {

    private Long rentId;
    private String email;
    private String nickname;
    private LocalDateTime rentDate;
    private List<BookInfo> books;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class BookInfo {
        private String title;
        private String writer;
        private int quantity;
    }

}
