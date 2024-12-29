package com.readhub.backend.rent;

import com.readhub.backend.book.Book;
import com.readhub.backend.book.BookRepository;
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
            throw new IllegalStateException("대출 가능한 도서가 없습니다.");
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
