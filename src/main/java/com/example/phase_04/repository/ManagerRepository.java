package com.example.phase_04.repository;

import com.example.phase_04.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager,Long> {

    Optional<Manager> findByUsername (String managerUsername);
}
