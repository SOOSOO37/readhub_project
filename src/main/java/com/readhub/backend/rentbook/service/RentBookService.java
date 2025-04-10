package com.readhub.backend.rentbook.service;

import com.readhub.backend.global.exception.BusinessLogicException;
import com.readhub.backend.global.exception.ExceptionCode;
import com.readhub.backend.rent.entity.Rent;
import com.readhub.backend.rentbook.entity.RentBook;
import com.readhub.backend.rentbook.repository.RentBookRepository;
import com.readhub.backend.user.entity.User;
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

    public Page<RentBook> getAllRentBooks(Pageable pageable) {
        return rentBookRepository.findAll(pageable);
    }

    public RentBook extendRent(RentBook rentBook, User user) {
        RentBook rentExtendBook = findVerifiedRentBook(rentBook.getRentBookId());
        Rent rent = rentBook.getRent();

        validateUser(rent, user.getId());
        validateExtensionCount(rent);

        processExtension(rent);
        return rentBookRepository.save(rentExtendBook);
    }

    private RentBook findVerifiedRentBook(Long rentBookId) {
        return rentBookRepository.findById(rentBookId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.RENTBOOK_NOT_FOUND));
    }

    private void validateUser(Rent rent, Long userId) {
        if (rent.getUser() == null || rent.getUser().getId() != userId) {
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
