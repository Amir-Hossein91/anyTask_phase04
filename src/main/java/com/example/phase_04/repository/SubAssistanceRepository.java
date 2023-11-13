package com.example.phase_04.repository;

import com.example.phase_04.entity.Assistance;
import com.example.phase_04.entity.SubAssistance;
import com.example.phase_04.entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubAssistanceRepository extends JpaRepository<SubAssistance,Long> {
    Optional<SubAssistance> findByTitleAndAssistance (String title, Assistance assistance);

    Optional<List<SubAssistance>> findByTechniciansContaining(Technician technician);

    Optional<List<SubAssistance>> findByTitle(String title);

    Optional<List<SubAssistance>> findByAssistance(Assistance assistance);
}
