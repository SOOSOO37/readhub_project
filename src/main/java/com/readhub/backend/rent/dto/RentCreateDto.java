package com.readhub.backend.rent.dto;

import com.readhub.backend.rent.entity.Rent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class RentCreateDto {

    @NotNull
    private Long bookId;

    @NotNull
    private LocalDate dueDate;

    @NotNull
    private Rent.RentStatus rentStatus;

    // 기본 생성자와 모든 필드를 포함한 생성자 제공
    public RentCreateDto(LocalDate dueDate, Rent.RentStatus rentStatus) {
        this.dueDate = dueDate;
        this.rentStatus = rentStatus;
    }
}
