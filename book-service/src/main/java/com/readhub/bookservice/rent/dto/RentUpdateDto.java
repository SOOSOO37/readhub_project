package com.readhub.bookservice.rent.dto;

import com.readhub.bookservice.rent.entity.Rent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RentUpdateDto {

    @NotNull(message = "대출 상태는 필수입니다.")
    private Rent.RentStatus rentStatus;
}
