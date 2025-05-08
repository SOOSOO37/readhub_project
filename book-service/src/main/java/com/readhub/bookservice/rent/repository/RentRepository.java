package com.readhub.bookservice.rent.repository;

import com.readhub.bookservice.book.entity.Book;
import com.readhub.bookservice.rent.entity.Rent;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentRepository extends JpaRepository<Rent,Long> {

    Page<Rent> findByRentStatus(Rent.RentStatus rentStatus, Pageable pageable);

    boolean existsByUserIdAndRentBookListBookId(Long userId, Long bookId);

    Page<Rent> findByUserId(Long userId, Pageable pageable);

    @Query(
            value = "SELECT b FROM RentBook rb JOIN rb.book b JOIN rb.rent r WHERE r.userId = :userId ORDER BY r.createdAt DESC",
            countQuery = "SELECT COUNT(b) FROM RentBook rb JOIN rb.book b JOIN rb.rent r WHERE r.userId = :userId")
    Page<Book> findRecentRentedBooks(@Param("userId") Long userId, Pageable pageable);

    List<Rent> findByDueDateAndRentStatus(LocalDate dueDate, Rent.RentStatus rentStatus);

    List<Rent> findByDueDateBeforeAndRentStatus(LocalDate today, Rent.RentStatus rentStatus);

    @Query("SELECT r FROM Rent r WHERE r.createdAt BETWEEN :start AND :end")
    List<Rent> findByCreatedAtBetween(@Param("start") LocalDateTime start,@Param("end") LocalDateTime end);

    @Query("SELECT r FROM Rent r WHERE r.createdAt BETWEEN :start AND :end")
    Page<Rent> findByCreatedAtBetween(@Param("start") LocalDateTime start,@Param("end") LocalDateTime end, Pageable pageable);

    @Query("SELECT r FROM Rent r " + "WHERE r.returnDate < :cutoffDate " + "AND r.rentStatus = 'RENT'")
    List<Rent> findOverdueRentRecords(@Param("cutoffDate") LocalDate cutoffDate);

}
