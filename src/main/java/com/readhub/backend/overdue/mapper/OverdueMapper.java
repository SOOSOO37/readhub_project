package com.readhub.backend.overdue.mapper;

import com.readhub.backend.overdue.dto.OverdueResponseDto;
import com.readhub.backend.overdue.entity.Overdue;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OverdueMapper {

    default OverdueResponseDto overdueToResponseDto(Overdue overdue) {
        return OverdueResponseDto.builder()
                .id(overdue.getId())
                .title(overdue.getBook().getTitle())
                .email(overdue.getUser().getEmail())
                .overdueStatus(overdue.getOverdueStatus())
                .build();
    }

    List<OverdueResponseDto> overdueToResponseDtos(List<Overdue> overdues);

}
