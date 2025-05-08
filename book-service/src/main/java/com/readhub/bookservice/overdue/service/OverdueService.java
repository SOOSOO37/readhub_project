package com.readhub.bookservice.overdue.service;

import com.readhub.bookservice.kafka.UserInfoKafkaService;
import com.readhub.global.exception.BusinessLogicException;
import com.readhub.global.exception.ExceptionCode;
import com.readhub.bookservice.overdue.entity.Overdue;
import com.readhub.bookservice.overdue.repository.OverdueRepository;
import com.readhub.bookservice.rent.entity.Rent;
import com.readhub.bookservice.rent.repository.RentRepository;
import com.readhub.bookservice.rentbook.entity.RentBook;
import com.readhub.global.kafka.UserInfoResponseEvent;
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
    private final UserInfoKafkaService userInfoKafkaService;


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
        UserInfoResponseEvent user = userInfoKafkaService.fetchUserInfoViaKafka(rent.getUserId());
        for (RentBook rentBook : rent.getRentBookList()) {
            if (rentBook.getRentBookStatus() != RentBook.RentBookStatus.RENT_FINISH) continue;

            Overdue overdue = Overdue.builder()
                    .book(rentBook.getBook())
                    .userId(user.getUserId())
                    .overdueDate(LocalDate.now())
                    .overdueStatus(Overdue.OverdueStatus.ACTIVE)
                    .build();
            overdueRepository.save(overdue);
            list.add(overdue);
        }
        return list;
    }

    public Page<Overdue> findUserOverdue (int page, int size,Long userId) {
        Page<Overdue> overduePage = overdueRepository.findByUserId(userId,PageRequest.of(page, size, Sort.by("id").descending()));
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
        List<Overdue> overdueList = overdueRepository.findByUserIdAndOverdueStatus(userId, Overdue.OverdueStatus.NOT_RENTABLE);
        return overdueList.size() >= 3 &&
                overdueList.stream().anyMatch(o -> ChronoUnit.DAYS.between(o.getOverdueDate(), LocalDate.now()) >= 10);
    }

    public void approveUnban(long userId) {
        List<Overdue> overdueList = overdueRepository.findByUserIdAndOverdueStatus(userId, Overdue.OverdueStatus.NOT_RENTABLE);
        for (Overdue o : overdueList) {
            o.setOverdueStatus(Overdue.OverdueStatus.CANCELED);
        }
        overdueRepository.saveAll(overdueList);
    }

    public Overdue findVerifiedOverdue (long id) {
        Optional<Overdue> optionalOverdue = overdueRepository.findById(id);
        Overdue findOverdue =
                optionalOverdue.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.NOT_OVERDUE));
        return findOverdue;
    }
}