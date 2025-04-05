package com.readhub.backend.rent.mapper;

import com.readhub.backend.book.entity.Book;
import com.readhub.backend.rent.dto.RentCreateDto;
import com.readhub.backend.rent.dto.RentResponseDto;
import com.readhub.backend.rent.dto.RentUpdateDto;
import com.readhub.backend.rent.entity.Rent;
import com.readhub.backend.rentbook.dto.RentBookDto;
import com.readhub.backend.rentbook.entity.RentBook;
import org.mapstruct.Mapper;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RentMapper {

   default Rent rentCreateDtoToRent (RentCreateDto rentCreateDto){
        Rent rent = new Rent();
        BeanUtils.copyProperties(rentCreateDto,rent);

        List<RentBook> rentBookDtoList = rentCreateDto.getRentBookDtoList().stream()
                .map(rentBookDto -> {
                    RentBook rentBook = new RentBook();
                    Book book = new Book();
                    book.setId(rentBookDto.getBookId());
                    rentBook.setRent(rent);
                    rentBook.setBook(book);
                    rentBook.setQuantity(rentBookDto.getQuantity());
                    return rentBook;
                }).collect(Collectors.toList());
        rent.setRentBookList(rentBookDtoList);
        return rent;
    }

    Rent rentUpdateDtoToRent (RentUpdateDto rentUpdateDto);

    RentResponseDto rentToRentResponseDto (Rent rent);

    List<RentResponseDto> rentsToRentResponseDtos(List<Rent> rentList);

}
