package com.readhub.backend.rentbook.mapper;

import com.readhub.backend.rent.entity.Rent;
import com.readhub.backend.rentbook.dto.RentBookResponseDto;
import com.readhub.backend.rentbook.entity.RentBook;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RentBookMapper {

    default RentBookResponseDto rentBookToRentBookResponseDto(RentBook rentBook) {
        RentBookResponseDto response = new RentBookResponseDto();

        if (rentBook.getBook() != null) {
            response.setTitle(rentBook.getBook().getTitle());
            response.setWriter(rentBook.getBook().getWriter());
        }

        if (rentBook.getRent() != null && rentBook.getRent().getUser() != null) {
            response.setNickName(rentBook.getRent().getUser().getNickName());
            response.setEmail(rentBook.getRent().getUser().getEmail());
        }

        return response;
    }

    default List<RentBookResponseDto> rentBooksToDtos(List<RentBook> rentBooks) {
        return rentBooks.stream()
                .map(this::rentBookToRentBookResponseDto)
                .toList();
    }
}
