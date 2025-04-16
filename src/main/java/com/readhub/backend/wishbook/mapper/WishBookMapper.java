package com.readhub.backend.wishbook.mapper;

import com.readhub.backend.wishbook.dto.WishBookCreateDto;
import com.readhub.backend.wishbook.dto.WishBookResponseDto;
import com.readhub.backend.wishbook.entity.WishBook;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WishBookMapper {

    default WishBook wishBookCreateDtoToWishBook(WishBookCreateDto dto) {
        WishBook wishBook = new WishBook();
        wishBook.setTitle(dto.getTitle());
        wishBook.setWriter(dto.getWriter());
        return wishBook;
    }

    default WishBookResponseDto wishBookToWishBookResponseDto(WishBook wishBook) {
        WishBookResponseDto response = new WishBookResponseDto();
        response.setId(wishBook.getId());
        response.setTitle(wishBook.getTitle());
        response.setWriter(wishBook.getWriter());
        response.setWishBookStatus(wishBook.getWishBookStatus());
        response.setEmail(wishBook.getUser().getEmail());
        return response;
    }

    List<WishBookResponseDto> wishBooksToWishBookResponseDtos(List<WishBook> wishBooks);

}
