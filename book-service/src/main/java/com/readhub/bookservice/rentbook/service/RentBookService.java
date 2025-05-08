package com.readhub.bookservice.rentbook.service;

import com.readhub.global.exception.BusinessLogicException;
import com.readhub.global.exception.ExceptionCode;
import com.readhub.bookservice.rent.entity.Rent;
import com.readhub.bookservice.rentbook.entity.RentBook;
import com.readhub.bookservice.rentbook.repository.RentBookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Transactional
@RequiredArgsConstructor
@Service
public class RentBookService {

    private final RentBookRepository rentBookRepository;

    public Page<RentBook> findAllRentBooks(Pageable pageable) {
        return rentBookRepository.findAll(pageable);
    }

    public RentBook extendRent(RentBook rentBook, Long userId) {
        RentBook rentExtendBook = findVerifiedRentBook(rentBook.getRentBookId());
        Rent rent = rentBook.getRent();

        validateUser(rent, userId);
        validateExtensionCount(rent);
        processExtension(rent);

        return rentBookRepository.save(rentExtendBook);
    }

    private RentBook findVerifiedRentBook(Long rentBookId) {
        return rentBookRepository.findById(rentBookId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.RENTBOOK_NOT_FOUND));
    }

    private void validateUser(Rent rent, Long userId) {
        if (rent.getUserId() == null || !rent.getUserId().equals(userId)) {
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }
    }

    private void validateExtensionCount (Rent rent) {
        if (rent.getExtensionCount() == 0) {
            throw new BusinessLogicException(ExceptionCode.ALREADY_EXTENDED);
        }

        if (rent.getReturnDate() != null && rent.getReturnDate().isBefore(LocalDate.now())) {
            throw new BusinessLogicException(ExceptionCode.OVERDUE_RENT);
        }
    }

    private void processExtension(Rent rent) {
        rent.setReturnDate(rent.getReturnDate().plusDays(7));
        rent.setExtensionCount(rent.getExtensionCount() - 1);
    }

}
