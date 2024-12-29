package com.readhub.backend.rent;

import com.readhub.backend.global.response.MultiResponseDto;
import com.readhub.backend.global.utils.UriCreator;
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
    public ResponseEntity createOrder (@RequestBody RentCreateDto rentCreateDto){

        Rent rent = service.createRent(mapper.rentCreateDtoToRent(rentCreateDto));
        URI location = UriCreator.createUri(RENT_DEFAULT_URL, rent.getId());

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{rentStatus}")
    public ResponseEntity findAllRent(@PathVariable Rent.RentStatus rentStatus,
                                      @RequestParam int page,
                                      @RequestParam int size) {

        Page<Rent> rentPage = service.findAllRent(rentStatus, page - 1, size);
        List<Rent> rentList = rentPage.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(mapper.rentsToRentResponseDtos(rentList), rentPage),
                HttpStatus.OK
        );
    }
}
