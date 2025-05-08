package com.readhub.bookservice.rentbook.mapper;


import com.readhub.bookservice.rentbook.dto.RentBookExtendDto;
import com.readhub.bookservice.rentbook.dto.RentBookExtendResponseDto;
import com.readhub.bookservice.rentbook.dto.RentBookResponseDto;
import com.readhub.bookservice.rentbook.entity.RentBook;
import com.readhub.global.kafka.UserInfoResponseEvent;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface RentBookMapper {

    default RentBookResponseDto rentBookToRentBookResponseDto(RentBook rentBook, UserInfoResponseEvent user) {
        RentBookResponseDto response = new RentBookResponseDto();

        if (rentBook.getBook() != null) {
            response.setTitle(rentBook.getBook().getTitle());
            response.setWriter(rentBook.getBook().getWriter());
        }
        if (user != null) {
            response.setEmail(user.getEmail());
        }
        return response;

    }

    default List<RentBookResponseDto> rentBooksToDtos(List<RentBook> rentBooks, Map<Long, UserInfoResponseEvent> userInfoMap) {
        return rentBooks.stream()
                .map(rentBook -> {
                    Long userId = rentBook.getRent().getUserId();
                    UserInfoResponseEvent user = userInfoMap.get(userId);
                    return rentBookToRentBookResponseDto(rentBook, user);
                })
                .toList();
    }

    RentBook rentBookExtendDtoToRentBook (RentBookExtendDto rentBookExtendDto);


    default RentBookExtendResponseDto rentBookToRentBookExtendResponseDto(RentBook rentBook, UserInfoResponseEvent user) {
        RentBookExtendResponseDto response = new RentBookExtendResponseDto();

        if (rentBook.getRent() != null) {
            response.setRentId(rentBook.getRent().getId());
            response.setReturnDate(rentBook.getRent().getReturnDate());
            response.setExtensionCount(rentBook.getRent().getExtensionCount());
        }

        if (user != null) {
            response.setEmail(user.getEmail());
        }
        return response;
    }
}
