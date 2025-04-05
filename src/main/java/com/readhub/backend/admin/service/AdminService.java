package com.readhub.backend.admin.service;

import com.readhub.backend.global.exception.BusinessLogicException;
import com.readhub.backend.global.exception.ExceptionCode;
import com.readhub.backend.rent.entity.Rent;
import com.readhub.backend.rent.repository.RentRepository;
import com.readhub.backend.user.entity.User;
import com.readhub.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RentRepository rentRepository;

    public Page<User> findUsers(int page, int size){

        Page<User> users = userRepository.findAllByUserStatus(User.UserStatus.ACTIVE,
                PageRequest.of(page, size, Sort.by("id").descending()));
        return users;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmailAndUserStatus(email, User.UserStatus.ACTIVE)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    }

    public User stopUser(Long id){
        User user = findVerifiedUser(id);

        if (user.getUserStatus() == User.UserStatus.STOP) {
            throw new BusinessLogicException(ExceptionCode.USER_STOP);
        }
        user.setUserStatus(User.UserStatus.STOP);
        userRepository.save(user);
        return user;
    }

    public Page<Rent> findAllRent (Rent.RentStatus rentStatus, int page, int size){
        Page<Rent> rentPage = rentRepository.findByRentStatus(rentStatus,
                PageRequest.of(page, size, Sort.by("id").descending()));
        return rentPage;
    }

    private User findVerifiedUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        User findUser = optionalUser.orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
        return findUser;
    }
}
