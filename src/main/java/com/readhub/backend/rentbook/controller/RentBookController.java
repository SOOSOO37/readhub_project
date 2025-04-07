package com.readhub.backend.rentbook.controller;

import com.readhub.backend.global.response.MultiResponseDto;
import com.readhub.backend.rentbook.dto.RentBookResponseDto;
import com.readhub.backend.rentbook.entity.RentBook;
import com.readhub.backend.rentbook.mapper.RentBookMapper;
import com.readhub.backend.rentbook.service.RentBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
