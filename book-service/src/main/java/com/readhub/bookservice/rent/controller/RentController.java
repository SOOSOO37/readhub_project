package com.readhub.bookservice.rent.controller;

import com.readhub.global.response.MultiResponseDto;
import com.readhub.global.security.userdetail.CustomUserDetails;
import com.readhub.global.utils.UriCreator;
import com.readhub.bookservice.rent.dto.RentCreateDto;
import com.readhub.bookservice.rent.mapper.RentMapper;
import com.readhub.bookservice.rent.service.RentService;
import com.readhub.bookservice.rent.entity.Rent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rents")
public class RentController {

    private final static String RENT_DEFAULT_URL = "/rents";

    private final RentMapper mapper;


    private final RentService service;

    @PostMapping
    public ResponseEntity createRent (@RequestBody RentCreateDto rentCreateDto,
                                      @AuthenticationPrincipal CustomUserDetails customUserDetails){

        rentCreateDto.setUserId(customUserDetails.getId());
        Rent rent = service.createRent(mapper.rentCreateDtoToRent(rentCreateDto),customUserDetails.getId());
        URI location = UriCreator.createUri(RENT_DEFAULT_URL, rent.getId());

        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity findAllRentByUser(@RequestParam int page,
                                            @RequestParam int size,
                                            @AuthenticationPrincipal CustomUserDetails user){

        Page<Rent> rentPage = service.findAllRent(page-1, size,user.getId());
        List<Rent> rentList = rentPage.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.rentsToRentResponseDtos(rentList),rentPage), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity findRent(@PathVariable long id,
                                    @AuthenticationPrincipal CustomUserDetails user) {
        Rent rent = service.findRent(user.getId(),id);
        return new ResponseEntity<>(mapper.rentToRentDetailResponseDto(rent), HttpStatus.OK);
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity cancelRent(@PathVariable long id,
                                      @AuthenticationPrincipal CustomUserDetails user){
        Rent rent = service.cancelRent(user.getId(),id);
        return new ResponseEntity<>(mapper.rentToRentResponseDto(rent),HttpStatus.OK);
    }

    @PatchMapping("/return/{id}")
    public ResponseEntity returnRentBook(@PathVariable("id") long id,
                                         @AuthenticationPrincipal CustomUserDetails user) {

        Rent rent = service.returnRentBook(user.getId(), id);
        return new ResponseEntity<>(mapper.rentToRentResponseDto(rent), HttpStatus.OK);
    }

    @GetMapping("/admin")
    public ResponseEntity findAllRentByAdmin(@RequestParam Rent.RentStatus rentStatus,
                                             @RequestParam int page,
                                             @RequestParam int size) {

        Page<Rent> rentPage = service.findAllRentByAdmin(rentStatus, page - 1, size);
        List<Rent> rentList = rentPage.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.rentsToRentResponseDtos(rentList), rentPage), HttpStatus.OK);
    }
}

