package com.readhub.bookservice.rentbook.repository;

import com.readhub.bookservice.rentbook.entity.RentBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RentBookRepository extends JpaRepository<RentBook,Long> {

    Page<RentBook> findAll(Pageable pageable);

    @Query("SELECT rb.book, COUNT(rb) as cnt " + "FROM RentBook rb " + "GROUP BY rb.book " + "ORDER BY cnt DESC")
    Page<Object[]> findMostRentedBooks(Pageable pageable);
}
