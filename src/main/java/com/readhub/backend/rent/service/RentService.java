package com.readhub.backend.rent.service;

import com.readhub.backend.book.entity.Book;
import com.readhub.backend.book.repository.BookRepository;
import com.readhub.backend.global.exception.BusinessLogicException;
import com.readhub.backend.global.exception.ExceptionCode;
import com.readhub.backend.rent.entity.Rent;
import com.readhub.backend.rent.repository.RentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class RentService {

    private final RentRepository rentRepository;
    private final BookRepository bookRepository;

    @Transactional
    public Rent createRent(Rent rent) {

        Book book = rent.getBook();
        if (book.getRentCount() <= 0) {
            throw new BusinessLogicException(ExceptionCode.BOOK_NOT_FOUND);
        }

        rent.setDueDate(LocalDate.now().plusDays(7));
        rent.setRentStatus(Rent.RentStatus.RENT);

        book.setRentCount(book.getRentCount() - 1);

        Rent savedRent = rentRepository.save(rent);

        return savedRent;
    }

    public Page<Rent> findAllRent (Rent.RentStatus rentStatus,int page, int size){
        Page<Rent> rentPage = rentRepository.findByRentStatus(rentStatus,
                PageRequest.of(page, size, Sort.by("id").descending()));
        return rentPage;
    }
}
