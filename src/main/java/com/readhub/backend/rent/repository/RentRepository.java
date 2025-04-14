package com.readhub.backend.rent.repository;

import com.readhub.backend.book.entity.Book;
import com.readhub.backend.rent.entity.Rent;
import com.readhub.backend.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentRepository extends JpaRepository<Rent,Long> {

    Page<Rent> findByRentStatus(Rent.RentStatus rentStatus, Pageable pageable);

    boolean existsByUserIdAndRentBookListBookId(Long userId, Long bookId);

    Page<Rent> findByUser(User user, Pageable pageable);

    @Query("SELECT rb.book FROM RentBook rb " + "JOIN rb.rent r " + "WHERE r.user.id = :userId " + "ORDER BY r.createdAt DESC")
    Optional<Book> findRecentRentedBookByUserId(@Param("userId") Long userId);

    List<Rent> findByDueDateAndRentStatus(LocalDate dueDate, Rent.RentStatus rentStatus);

    List<Rent> findByDueDateBeforeAndRentStatus(LocalDate today, Rent.RentStatus rentStatus);
}
