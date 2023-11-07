package com.example.phase_04.repository;

import com.example.phase_04.entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician,Long> {

    Optional<Technician> findByUsername (String technicianUsername);

    @Query(value = "from Technician where technicianStatus <> 'APPROVED'")
    Optional<List<Technician>> findUnapproved();

    @Query(value = "from Technician where technicianStatus = 'APPROVED' and isActive = false")
    Optional<List<Technician>> findDeactivated();
}
