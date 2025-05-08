package com.readhub.bookservice.rentbook.controller;

import com.readhub.bookservice.kafka.UserInfoKafkaService;
import com.readhub.bookservice.rentbook.dto.RentBookExtendResponseDto;
import com.readhub.global.kafka.UserInfoResponseEvent;
import com.readhub.global.response.MultiResponseDto;
import com.readhub.bookservice.rentbook.dto.RentBookExtendDto;
import com.readhub.bookservice.rentbook.entity.RentBook;
import com.readhub.bookservice.rentbook.mapper.RentBookMapper;
import com.readhub.bookservice.rentbook.service.RentBookService;
import com.readhub.global.security.userdetail.CustomUserDetails;
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
    private final UserInfoKafkaService userInfoKafkaService;

    @GetMapping("/find")
    public ResponseEntity<?> getAllRentBooks(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size) {

        Page<RentBook> rentBookPage = rentBookService.findAllRentBooks(PageRequest.of(page - 1, size));
        List<RentBook> rentBookList = rentBookPage.getContent();

        return ResponseEntity.ok(new MultiResponseDto<>(rentBookList, rentBookPage));
    }

    @PatchMapping("/extend/{rentBookId}")
    public ResponseEntity extendRent(@PathVariable Long rentBookId,
                                     @RequestBody RentBookExtendDto rentBookExtendDto,
                                     @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        rentBookExtendDto.setRentBookId(rentBookId);
        rentBookExtendDto.setUserId(customUserDetails.getId());

        RentBook rentBook = rentBookMapper.rentBookExtendDtoToRentBook(rentBookExtendDto);
        RentBook extended = rentBookService.extendRent(rentBook, customUserDetails.getId());

        UserInfoResponseEvent user = userInfoKafkaService.fetchUserInfoViaKafka(customUserDetails.getId());

        RentBookExtendResponseDto response = rentBookMapper.rentBookToRentBookExtendResponseDto(extended, user);
        return ResponseEntity.ok(response);

    }
}
