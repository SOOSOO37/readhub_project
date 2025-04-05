package com.readhub.backend.user.repository;

import com.readhub.backend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByNickName(String nickname);

    Page<User> findAllByUserStatus(User.UserStatus userStatus, Pageable pageable);

    Optional<User> findByEmailAndUserStatus(String email, User.UserStatus userStatus);
}
