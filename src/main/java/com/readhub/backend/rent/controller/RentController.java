package com.readhub.backend.rent.controller;

import com.readhub.backend.global.response.MultiResponseDto;
import com.readhub.backend.global.utils.UriCreator;
import com.readhub.backend.rent.dto.RentCreateDto;
import com.readhub.backend.rent.mapper.RentMapper;
import com.readhub.backend.rent.service.RentService;
import com.readhub.backend.rent.entity.Rent;
import com.readhub.backend.user.entity.User;
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
                                      @AuthenticationPrincipal User user){

        rentCreateDto.setUserId(user.getId());
        Rent rent = service.createRent(mapper.rentCreateDtoToRent(rentCreateDto),user);
        URI location = UriCreator.createUri(RENT_DEFAULT_URL, rent.getId());

        return ResponseEntity.created(location).build();
    }
    //
    @GetMapping
    public ResponseEntity findAllRent(@RequestParam int page,
                                       @RequestParam int size,
                                       @AuthenticationPrincipal User user){

        Page<Rent> rentPage = service.findAllRent(page-1, size,user);
        List<Rent> rentList = rentPage.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.rentsToRentResponseDtos(rentList),rentPage), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity findRent(@PathVariable long id,
                                    @AuthenticationPrincipal User user) {
        Rent rent = service.findRent(user,id);
        return new ResponseEntity<>(mapper.rentToRentDetailResponseDto(rent), HttpStatus.OK);
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity cancelRent(@PathVariable long id,
                                      @AuthenticationPrincipal User user){
        Rent rent = service.cancelRent(user,id);
        return new ResponseEntity<>(mapper.rentToRentResponseDto(rent),HttpStatus.OK);
    }

    @PatchMapping("/return/{id}")
    public ResponseEntity returnRentBook(@PathVariable("id") long id,
                                         @AuthenticationPrincipal User user) {

        Rent rent = service.returnRentBook(user, id);
        return new ResponseEntity<>(mapper.rentToRentResponseDto(rent), HttpStatus.OK);
    }

}
