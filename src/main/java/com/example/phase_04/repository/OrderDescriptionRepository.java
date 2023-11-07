package com.example.phase_04.repository;

import com.example.phase_04.entity.OrderDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDescriptionRepository extends JpaRepository<OrderDescription,Long> {
}
