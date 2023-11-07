package com.example.phase_04.service.impl;

import com.example.phase_04.entity.Manager;
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
    private final ManagerServiceImpl managerService;

    public TechnicianSuggestionServiceImpl(TechnicianSuggestionRepository repository, ManagerServiceImpl managerService) {
        super();
        this.repository = repository;
        this.managerService = managerService;
    }


    public List<String> showAllSuggestions(String managerUsername) {
        Manager manager = managerService.findByUsername(managerUsername);
        if (manager == null)
            throw new IllegalArgumentException("Only manager can see the list of all technician suggestion");
        return findAll().stream().map(Object::toString).toList();

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
