package com.readhub.backend.rent;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentRepository extends JpaRepository<Rent,Long> {

    Page<Rent> findByRentStatus(Rent.RentStatus rentStatus, Pageable pageable);
}
