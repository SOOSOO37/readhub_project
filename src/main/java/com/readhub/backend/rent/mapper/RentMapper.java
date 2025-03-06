package com.readhub.backend.rent.mapper;

import com.readhub.backend.rent.dto.RentCreateDto;
import com.readhub.backend.rent.dto.RentResponseDto;
import com.readhub.backend.rent.dto.RentUpdateDto;
import com.readhub.backend.rent.entity.Rent;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RentMapper {

    Rent rentCreateDtoToRent (RentCreateDto rentCreateDto);

    Rent rentUpdateDtoToRent (RentUpdateDto rentUpdateDto);

    RentResponseDto rentToRentResponseDto (Rent rent);

    List<RentResponseDto> rentsToRentResponseDtos(List<Rent> rentList);

}
