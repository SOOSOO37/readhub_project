package com.readhub.bookservice.overdue.mapper;

import com.readhub.bookservice.overdue.dto.OverdueResponseDto;
import com.readhub.bookservice.overdue.entity.Overdue;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OverdueMapper {

    default OverdueResponseDto overdueToResponseDto(Overdue overdue) {
        return OverdueResponseDto.builder()
                .id(overdue.getId())
                .title(overdue.getBook().getTitle())
                .email(overdue.getEmail())
                .overdueStatus(overdue.getOverdueStatus())
                .build();
    }

    List<OverdueResponseDto> overdueToResponseDtos(List<Overdue> overdues);

}
