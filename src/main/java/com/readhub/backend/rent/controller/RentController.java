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

}
