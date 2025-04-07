package com.readhub.backend.rentbook.service;

import com.readhub.backend.rentbook.dto.RentBookResponseDto;
import com.readhub.backend.rentbook.entity.RentBook;
import com.readhub.backend.rentbook.repository.RentBookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
public class RentBookService {

    private final RentBookRepository rentBookRepository;

    public Page<RentBook> getAllRentBooks(Pageable pageable) {
        return rentBookRepository.findAll(pageable);
    }
}
