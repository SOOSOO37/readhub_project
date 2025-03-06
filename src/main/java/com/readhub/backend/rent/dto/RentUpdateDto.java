package com.readhub.backend.rent.dto;

import com.readhub.backend.rent.entity.Rent;
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
