package com.readhub.backend.statistics.controller;

import com.readhub.backend.book.entity.Book;
import com.readhub.backend.global.response.MultiResponseDto;
import com.readhub.backend.rent.entity.Rent;
import com.readhub.backend.reservation.mapper.ReservationMapper;
import com.readhub.backend.statistics.dto.MostRentedBookResponseDto;
import com.readhub.backend.statistics.dto.RentCountResponseDto;
import com.readhub.backend.statistics.dto.RentListResponseDto;
import com.readhub.backend.statistics.mapper.StatisticsMapper;
import com.readhub.backend.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/statistics")
@RestController
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final StatisticsMapper mapper;

    @GetMapping("/rent-count")
    public ResponseEntity findRentCountByPeriod(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<Rent> rentList = statisticsService.findRentsByPeriod(startDate, endDate);
        RentCountResponseDto responseDto = mapper.rentsToRentCountResponseDto(startDate, endDate, rentList);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/rent-list")
    public ResponseEntity findRentListByPeriod(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                 @RequestParam int page,
                                                 @RequestParam int size) {

        Page<Rent> rentPage = statisticsService.findRentListByPeriod(startDate, endDate, page - 1, size);
        List<Rent> rentList = rentPage.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.rentsToRentListResponseDtos(rentList), rentPage), HttpStatus.OK);
    }

    @GetMapping("/overdue-list")
    public ResponseEntity findOverdueList(@RequestParam int page,
                                          @RequestParam int size) {

        Page<Rent> rentPage = statisticsService.findOverdueRentPage(page - 1, size);
        List<Rent> overdueRents = rentPage.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.rentsToRentListResponseDtos(overdueRents), rentPage), HttpStatus.OK);

    }

    @GetMapping("/status-list")
    public ResponseEntity<MultiResponseDto<RentListResponseDto>> findRentListByStatus( @RequestParam("status") Rent.RentStatus status,
                                                                                       @RequestParam int page,
                                                                                       @RequestParam int size) {

        Page<Rent> rentPage = statisticsService.findRentPageByStatus(status, page - 1, size);
        List<Rent> rentList = rentPage.getContent();

        return ResponseEntity.ok(new MultiResponseDto<>(mapper.rentsToRentListResponseDtos(rentList), rentPage));
    }

    @GetMapping("/most-rented-books")
    public ResponseEntity findMostRentedBooks(@RequestParam int page,
                                              @RequestParam int size) {

        Page<Object[]> pageResult = statisticsService.findMostRentedBookPage(page - 1, size);
        List<MostRentedBookResponseDto> response = mapper.toMostRentedBookDtos(pageResult.getContent());

        return ResponseEntity.ok(new MultiResponseDto<>(response, pageResult));
    }

    @GetMapping("/popular")
    public ResponseEntity getPopularBooksByCategory(@RequestParam String category,
                                                    @RequestParam int page,
                                                    @RequestParam int size) {

        Page<Book> bookPage = statisticsService.findPopularBooksByCategory(category, page - 1, size);
        List<Book> books = bookPage.getContent();

        return ResponseEntity.ok(new MultiResponseDto<>(mapper.booksToPopularBookResponseDtos(books), bookPage));
    }


}
