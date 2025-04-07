package com.readhub.backend.rent.mapper;

import com.readhub.backend.book.entity.Book;
import com.readhub.backend.rent.dto.RentCreateDto;
import com.readhub.backend.rent.dto.RentDetailResponseDto;
import com.readhub.backend.rent.dto.RentResponseDto;
import com.readhub.backend.rent.dto.RentUpdateDto;
import com.readhub.backend.rent.entity.Rent;
import com.readhub.backend.rentbook.entity.RentBook;
import org.mapstruct.Mapper;
import org.springframework.beans.BeanUtils;

import java.util.List;
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

    List<RentResponseDto> rentsToRentResponseDtos(List<Rent> rentList);

    default RentDetailResponseDto rentToRentDetailResponseDto(Rent rent) {
        RentDetailResponseDto dto = new RentDetailResponseDto();
        BeanUtils.copyProperties(rent, dto);

        dto.setCreatedAt(rent.getCreatedAt().toLocalDate());
        dto.setModifiedAt(rent.getModifiedAt().toLocalDate());
        dto.setRentStatus(rent.getRentStatus().name());

        List<RentDetailResponseDto.RentedBookDto> rentedBooks = rent.getRentBookList().stream()
                .map(rentBook -> {
                    RentDetailResponseDto.RentedBookDto bookDto = new RentDetailResponseDto.RentedBookDto();
                    bookDto.setTitle(rentBook.getBook().getTitle());
                    return bookDto;
                })
                .collect(Collectors.toList());

        dto.setRentedBooks(rentedBooks);

        return dto;
    }

    default RentResponseDto rentToRentResponseDto(Rent rent) {
        RentResponseDto dto = new RentResponseDto();
        BeanUtils.copyProperties(rent, dto);

        dto.setCreatedAt(rent.getCreatedAt().toLocalDate());

        List<RentDetailResponseDto.RentedBookDto> rentedBooks = rent.getRentBookList().stream()
                .map(rentBook -> {
                    RentDetailResponseDto.RentedBookDto bookDto = new RentDetailResponseDto.RentedBookDto();
                    bookDto.setTitle(rentBook.getBook().getTitle());
                    return bookDto;
                })
                .collect(Collectors.toList());

        dto.setRentedBooks(rentedBooks);

        return dto;
    }

}
