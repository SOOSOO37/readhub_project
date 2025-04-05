package com.readhub.backend.rent.service;

import com.readhub.backend.book.entity.Book;
import com.readhub.backend.book.repository.BookRepository;
import com.readhub.backend.book.service.BookService;
import com.readhub.backend.global.exception.BusinessLogicException;
import com.readhub.backend.global.exception.ExceptionCode;
import com.readhub.backend.rent.entity.Rent;
import com.readhub.backend.rent.repository.RentRepository;
import com.readhub.backend.rentbook.entity.RentBook;
import com.readhub.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class RentService {

    private final RentRepository rentRepository;
    private final BookService bookService;

    @Transactional
    public Rent createRent(Rent rent, User user) {

        if(user == null){
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }
        List<RentBook> rentBookList = minusRentCount(rent);
        rent.setRentBookList(rentBookList);

        rent.setUser(user);

        Rent savedRent = rentRepository.save(rent);
        return savedRent;
    }

    public List<RentBook> minusRentCount(Rent rent){
        List<RentBook> rentBookList = rent.getRentBookList().stream()
                .map(rentBook -> {
                    Book book = bookService.findVerifiedBooks(rentBook.getBook().getId());

                    if (book.getRentCount() < rentBook.getQuantity()) {
                        throw new BusinessLogicException(ExceptionCode.BOOK_NOT_AVAILABLE);
                    }

                    long minusCount = book.getRentCount() - rentBook.getQuantity();
                    book.setRentCount((int) minusCount);
                    rentBook.setBook(book);
                    return rentBook;
                })
                .collect(Collectors.toList());
        return rentBookList;
    }
}
