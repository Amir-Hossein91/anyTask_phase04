package com.example.phase_04.service.impl;

import com.example.phase_04.entity.Order;
import com.example.phase_04.entity.TechnicianSuggestion;
import com.example.phase_04.exceptions.NotFoundException;
import com.example.phase_04.repository.TechnicianSuggestionRepository;
import com.example.phase_04.service.TechnicianSuggestionService;
import com.example.phase_04.utility.Constants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TechnicianSuggestionServiceImpl implements TechnicianSuggestionService {

    private final TechnicianSuggestionRepository repository;

    public TechnicianSuggestionServiceImpl(TechnicianSuggestionRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    @Transactional
    public TechnicianSuggestion saveOrUpdate(TechnicianSuggestion t) {
        return repository.save(t);
    }

    @Override
    @Transactional
    public void delete(TechnicianSuggestion t) {
        repository.delete(t);
    }

    @Override
    public TechnicianSuggestion findById(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("\nCould not find technician suggestion with id = " + id));
    }

    @Override
    public List<TechnicianSuggestion> findAll() {
        return repository.findAll();
    }

    @Override
    public List<TechnicianSuggestion> getSuggestionsOrderedByPrice(Order order) {
        return repository.findByOrderOrderByTechSuggestedPriceAsc(order).orElseThrow(
                () -> new NotFoundException(Constants.NO_TECHNICIAN_SUGGESTION_FOUND)
        );
    }

    @Override
    public List<TechnicianSuggestion> getSuggestionsOrderedByScore(Order order) {
        return repository.findByOrderOrderByTechnicianScore(order).orElseThrow(
                () -> new NotFoundException(Constants.NO_TECHNICIAN_SUGGESTION_FOUND)
        );
    }
}
