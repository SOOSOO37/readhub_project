package com.readhub.backend.rent.service;

import com.readhub.backend.book.entity.Book;
import com.readhub.backend.book.repository.BookRepository;
import com.readhub.backend.book.service.BookService;
import com.readhub.backend.global.exception.BusinessLogicException;
import com.readhub.backend.global.exception.ExceptionCode;
import com.readhub.backend.notification.service.NotificationService;
import com.readhub.backend.rent.entity.Rent;
import com.readhub.backend.rent.repository.RentRepository;
import com.readhub.backend.rentbook.entity.RentBook;
import com.readhub.backend.reservation.service.ReservationService;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class RentService {

    private final RentRepository rentRepository;
    private final BookService bookService;
    private final ReservationService reservationService;

    @Transactional
    public Rent createRent(Rent rent, User user) {

        if(user == null){
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }

        LocalDate now = LocalDate.now();
        rent.setDueDate(now.plusDays(14));
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

    public Page<Rent> findAllRent (int page, int size, User user){

        Page<Rent> rentPage = rentRepository.findByUser(user, PageRequest.of(page, size, Sort.by("id").descending()));
        return rentPage;
    }

    public Rent findRent (User user,long id){
        Rent findRent = findVerifiedRent(id);
        verifyRentUser(id, user.getId());
        return findRent;
    }

    public Rent cancelRent (User user,long rentId){
        Rent findRent = findVerifiedRent(rentId);
        verifyRentUser(rentId, user.getId());
        findRent.setRentStatus(Rent.RentStatus.CANCEL);
        Rent canceledRent = rentRepository.save(findRent);

        return canceledRent;
    }

    public Rent returnRentBook(User user, long rentId) {
        Rent rent = findVerifiedRent(rentId);
        verifyRentUser(rentId, user.getId());

        verifyReturn(rent);

        rent.setReturnDate(LocalDate.now());
        updateReturnStatus(rent);
        increaseRentCountAndCheckReservation(rent);

        return rentRepository.save(rent);
    }

    private void verifyReturn(Rent rent) {
        if (rent.getRentStatus() == Rent.RentStatus.RETURNED) {
            throw new BusinessLogicException(ExceptionCode.RETURN_NOT_AVAILABLE);
        }
    }

    private void updateReturnStatus(Rent rent) {
        if (rent.getDueDate().isBefore(LocalDate.now())) {
            rent.setRentStatus(Rent.RentStatus.OVERDUE);
        } else {
            rent.setRentStatus(Rent.RentStatus.RETURNED);
        }
    }

    private void increaseRentCountAndCheckReservation(Rent rent) {
        for (RentBook rentBook : rent.getRentBookList()) {
            Book book = rentBook.getBook();
            int previous = book.getRentCount();
            int updated = previous + rentBook.getQuantity();
            book.setRentCount(updated);

            if (previous == 0 && updated > 0) {
                reservationService.notifyAvailableToRent(book);
            }
        }
    }

    public Rent findVerifiedRent (long id){
        Optional<Rent> findRent = rentRepository.findById(id);
        Rent rent = findRent.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.RENT_NOT_FOUND));
        return rent;
    }

    public void verifyRentUser(long rentId, long userId){
        Rent findRent = findVerifiedRent(rentId);
        long dbUserId = findRent.getUser().getId();

        if(userId != dbUserId){
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }
    }

}
