package com.readhub.bookservice.rent.service;

import com.readhub.bookservice.book.entity.Book;
import com.readhub.bookservice.book.service.BookService;
import com.readhub.bookservice.kafka.UserInfoKafkaService;
import com.readhub.global.exception.BusinessLogicException;
import com.readhub.global.exception.ExceptionCode;
import com.readhub.bookservice.rent.entity.Rent;
import com.readhub.bookservice.rent.repository.RentRepository;
import com.readhub.bookservice.rentbook.entity.RentBook;
import com.readhub.bookservice.reservation.service.ReservationService;
import com.readhub.global.kafka.UserInfoResponseEvent;
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
    private final UserInfoKafkaService userInfoKafkaService;

    @Transactional
    public Rent createRent(Rent rent,Long userId) {
        UserInfoResponseEvent user = userInfoKafkaService.fetchUserInfoViaKafka(userId);
        LocalDate now = LocalDate.now();
        rent.setDueDate(now.plusDays(14));
        List<RentBook> rentBookList = minusRentCount(rent);
        rent.setRentBookList(rentBookList);

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

    public Page<Rent> findAllRent (int page, int size, Long userId){
        UserInfoResponseEvent userInfo = userInfoKafkaService.fetchUserInfoViaKafka(userId);

        return rentRepository.findByUserId(userInfo.getUserId(), PageRequest.of(page, size, Sort.by("id").descending()));
    }

    public Rent findRent (long userId,long rentId){
        Rent findRent = findVerifiedRent(rentId);
        verifyRentUser(rentId, userId);
        return findRent;
    }

    public Rent cancelRent (long userId,long rentId){
        Rent findRent = findVerifiedRent(rentId);
        verifyRentUser(rentId, userId);
        findRent.setRentStatus(Rent.RentStatus.CANCEL);
        Rent canceledRent = rentRepository.save(findRent);

        return canceledRent;
    }

    public Rent returnRentBook(long userId, long rentId) {
        Rent rent = findVerifiedRent(rentId);
        verifyRentUser(rentId, userId);

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

    public void verifyRentUser(long rentId, long userId) {
        Rent rent = findVerifiedRent(rentId);

        if (userId != rent.getUserId()) {
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }
        UserInfoResponseEvent response = userInfoKafkaService.fetchUserInfoViaKafka(userId);

        if (response == null) {
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }
    }

    public Page<Rent> findAllRentByAdmin (Rent.RentStatus rentStatus, int page, int size){
        Page<Rent> rentPage = rentRepository.findByRentStatus(rentStatus, PageRequest.of(page, size, Sort.by("id").descending()));
        return rentPage;
    }
}
