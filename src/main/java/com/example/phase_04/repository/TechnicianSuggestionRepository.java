package com.example.phase_04.repository;

import com.example.phase_04.entity.Order;
import com.example.phase_04.entity.TechnicianSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnicianSuggestionRepository extends JpaRepository<TechnicianSuggestion,Long> {
    Optional<List<TechnicianSuggestion>> findByOrderOrderByTechSuggestedPriceAsc(Order order);

    @Query(value = "from TechnicianSuggestion t where t.order =?1 order by t.technician.score desc")
    Optional<List<TechnicianSuggestion>> findByOrderOrderByTechnicianScore(Order order);
}
