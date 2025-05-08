package com.readhub.bookservice.overdue.repository;

import com.readhub.bookservice.overdue.entity.Overdue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OverdueRepository extends JpaRepository<Overdue,Long> {

    Page<Overdue> findByUserId(Long userId, Pageable pageable);
    List<Overdue> findByUserIdAndOverdueStatus(Long userId, Overdue.OverdueStatus overdueStatus);

}
