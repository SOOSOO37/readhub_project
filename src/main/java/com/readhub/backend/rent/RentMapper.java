package com.readhub.backend.rent;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RentMapper {

    Rent rentCreateDtoToRent (RentCreateDto rentCreateDto);

    Rent rentUpdateDtoToRent (RentUpdateDto rentUpdateDto);

    RentResponseDto rentToRentResponseDto (Rent rent);

    List<RentResponseDto> rentsToRentResponseDtos(List<Rent> rentList);

}
