package com.example.phase_04.service.impl;

import com.example.phase_04.entity.Assistance;
import com.example.phase_04.exceptions.DuplicateAssistanceException;
import com.example.phase_04.exceptions.NotFoundException;
import com.example.phase_04.repository.AssistanceRepository;
import com.example.phase_04.service.AssistanceService;
import com.example.phase_04.utility.Constants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssistanceServiceImpl implements AssistanceService {
    private final AssistanceRepository repository;


    public AssistanceServiceImpl(AssistanceRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    @Transactional
    public Assistance saveOrUpdate(Assistance t) {
        return repository.save(t);
    }

    @Override
    @Transactional
    public void delete(Assistance t) {
        repository.delete(t);
    }

    @Override
    public Assistance findById(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("\nCould not find assistance with id = " + id));
    }

    @Override
    public List<Assistance> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Assistance findAssistance(String assistanceName) {
        return repository.findByTitle(assistanceName).orElse(null);
    }

    public Assistance addAssistance(Assistance assistance) {
        if (findAssistance(assistance.getTitle()) != null)
            throw new DuplicateAssistanceException(Constants.ASSISTANCE_ALREADY_EXISTS);
        return saveOrUpdate(assistance);
    }
}
