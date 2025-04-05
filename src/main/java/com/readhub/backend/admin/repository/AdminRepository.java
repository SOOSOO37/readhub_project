package com.readhub.backend.admin.repository;

import com.readhub.backend.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin,Long> {

    Optional<Admin> findByEmail(String email);
}
