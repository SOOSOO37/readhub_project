package com.readhub.bookservice.statistics.service;

import com.readhub.bookservice.book.entity.Book;
import com.readhub.bookservice.book.repository.BookRepository;
import com.readhub.bookservice.rent.entity.Rent;
import com.readhub.bookservice.rent.repository.RentRepository;
import com.readhub.bookservice.rentbook.repository.RentBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StatisticsService {

    private final RentRepository rentRepository;
    private final RentBookRepository rentBookRepository;
    private final BookRepository bookRepository;

    public List<Rent> findRentsByPeriod(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        return rentRepository.findByCreatedAtBetween(start, end);
    }

    public Page<Rent> findRentListByPeriod(LocalDate startDate, LocalDate endDate, int page, int size) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return rentRepository.findByCreatedAtBetween(start, end, pageable);
    }

    public Page<Rent> findOverdueRentPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());
        return rentRepository.findByRentStatus(Rent.RentStatus.OVERDUE, pageable);
    }

    public Page<Rent> findRentPageByStatus(Rent.RentStatus rentStatus, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return rentRepository.findByRentStatus(rentStatus, pageable);
    }

    public Page<Object[]> findMostRentedBookPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return rentBookRepository.findMostRentedBooks(pageable);
    }

    public Page<Book> findPopularBooksByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByCategoryOrderByViewCountDesc(category, pageable);
    }

}
