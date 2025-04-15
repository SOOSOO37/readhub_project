package com.readhub.backend.overdue.service;

import com.readhub.backend.book.entity.Book;
import com.readhub.backend.book.repository.BookRepository;
import com.readhub.backend.global.exception.BusinessLogicException;
import com.readhub.backend.global.exception.ExceptionCode;
import com.readhub.backend.overdue.dto.OverdueResponseDto;
import com.readhub.backend.overdue.entity.Overdue;
import com.readhub.backend.overdue.mapper.OverdueMapper;
import com.readhub.backend.overdue.repository.OverdueRepository;
import com.readhub.backend.rent.entity.Rent;
import com.readhub.backend.rent.repository.RentRepository;
import com.readhub.backend.rentbook.entity.RentBook;
import com.readhub.backend.user.entity.User;
import com.readhub.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OverdueService {

    private final OverdueRepository overdueRepository;
    private final RentRepository rentRepository;


    public List<Overdue> autoRegisterOverdue() {
        List<Overdue> result = new ArrayList<>();
        List<Rent> overdueRents = rentRepository.findOverdueRentRecords(LocalDate.now().minusDays(7));
        for (Rent rent : overdueRents) {
            if (isValidRentForOverdue(rent)) {
                result.addAll(registerOverduesFromRent(rent));
            }
        }
        return result;
    }

    private boolean isValidRentForOverdue(Rent rent) {
        return rent.getRentStatus() == Rent.RentStatus.RENT;
    }

    private List<Overdue> registerOverduesFromRent(Rent rent) {
        List<Overdue> list = new ArrayList<>();
        for (RentBook rentBook : rent.getRentBookList()) {
            if (rentBook.getRentBookStatus() != RentBook.RentBookStatus.RENT_FINISH) continue;

            Overdue overdue = Overdue.builder()
                    .book(rentBook.getBook())
                    .user(rent.getUser())
                    .overdueDate(LocalDate.now())
                    .overdueStatus(Overdue.OverdueStatus.ACTIVE)
                    .build();
            overdueRepository.save(overdue);
            list.add(overdue);
        }
        return list;
    }

    public Page<Overdue> findUserOverdue (int page, int size,User user) {
        Page<Overdue> overduePage = overdueRepository.findByUser(user,PageRequest.of(page, size, Sort.by("id").descending()));
        return  overduePage;
    }

    public Page<Overdue> findAllOverdue(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return overdueRepository.findAll(pageable);
    }

    public void cancelOverdue(long overdueId) {
        Overdue overdue = findVerifiedOverdue(overdueId);
        overdue.setOverdueStatus(Overdue.OverdueStatus.CANCELED);
        overdueRepository.save(overdue);
    }

    public void updateNotRentable(long overdueId) {
        Overdue overdue = findVerifiedOverdue(overdueId);
        overdue.setOverdueStatus(Overdue.OverdueStatus.NOT_RENTABLE);
        overdueRepository.save(overdue);
    }

    public boolean canRequestUnban(long userId) {
        List<Overdue> overdues = overdueRepository.findByUserIdAndOverdueStatus(userId, Overdue.OverdueStatus.NOT_RENTABLE);
        return overdues.size() >= 3 &&
                overdues.stream().anyMatch(o -> ChronoUnit.DAYS.between(o.getOverdueDate(), LocalDate.now()) >= 10);
    }

    public void approveUnban(long userId) {
        List<Overdue> overdues = overdueRepository.findByUserIdAndOverdueStatus(userId, Overdue.OverdueStatus.NOT_RENTABLE);
        for (Overdue o : overdues) {
            o.setOverdueStatus(Overdue.OverdueStatus.CANCELED);
        }
        overdueRepository.saveAll(overdues);
    }

    public Overdue findVerifiedOverdue (long id) {
        Optional<Overdue> optionalOverdue = overdueRepository.findById(id);
        Overdue findOverdue =
                optionalOverdue.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.NOT_OVERDUE));
        return findOverdue;
    }
}