package com.readhub.backend.rentbook.controller;

import com.readhub.backend.global.response.MultiResponseDto;
import com.readhub.backend.rentbook.dto.RentBookExtendDto;
import com.readhub.backend.rentbook.dto.RentBookResponseDto;
import com.readhub.backend.rentbook.entity.RentBook;
import com.readhub.backend.rentbook.mapper.RentBookMapper;
import com.readhub.backend.rentbook.service.RentBookService;
import com.readhub.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RequestMapping("/rent-books")
@RestController
public class RentBookController {

    private final RentBookService rentBookService;
    private final RentBookMapper rentBookMapper;

    @GetMapping("/find")
    public ResponseEntity<?> getAllRentBooks(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size) {

        Page<RentBook> rentBookPage = rentBookService.getAllRentBooks(PageRequest.of(page - 1, size));
        List<RentBook> rentBookList = rentBookPage.getContent();

        return ResponseEntity.ok(new MultiResponseDto<>(rentBookList, rentBookPage));
    }

    @PatchMapping("/extend/{rentBookId}")
    public ResponseEntity extendRent(@PathVariable Long rentBookId,
                                     @RequestBody RentBookExtendDto rentBookExtendDto,
                                     @AuthenticationPrincipal User user) {

        rentBookExtendDto.setRentBookId(rentBookId);
        rentBookExtendDto.setUserId(user.getId());

        RentBook extendRent = rentBookService.extendRent(rentBookMapper.rentBookExtendDtoToRentBook(rentBookExtendDto), user);

        return new ResponseEntity<>(rentBookMapper.rentBookToRentBookExtendResponseDto(extendRent), HttpStatus.OK);
    }
}
