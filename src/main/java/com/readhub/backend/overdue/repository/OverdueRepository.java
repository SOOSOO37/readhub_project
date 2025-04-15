package com.readhub.backend.overdue.repository;

import com.readhub.backend.overdue.entity.Overdue;
import com.readhub.backend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OverdueRepository extends JpaRepository<Overdue,Long> {

    Page<Overdue> findByUser(User user, Pageable pageable);
    List<Overdue> findByUserIdAndOverdueStatus(Long userId, Overdue.OverdueStatus overdueStatus);
}
