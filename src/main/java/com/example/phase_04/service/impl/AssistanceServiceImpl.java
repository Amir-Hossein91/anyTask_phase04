package com.example.phase_04.service.impl;

import com.example.phase_04.entity.Assistance;
import com.example.phase_04.entity.Person;
import com.example.phase_04.entity.Technician;
import com.example.phase_04.exceptions.DeactivatedTechnicianException;
import com.example.phase_04.exceptions.DuplicateAssistanceException;
import com.example.phase_04.exceptions.NotFoundException;
import com.example.phase_04.repository.AssistanceRepository;
import com.example.phase_04.service.AssistanceService;
import com.example.phase_04.utility.Constants;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssistanceServiceImpl implements AssistanceService {
    private final AssistanceRepository repository;

    private final PersonServiceImpl personService;

    public AssistanceServiceImpl(AssistanceRepository repository,
                                 @Lazy PersonServiceImpl personService) {
        super();
        this.repository = repository;
        this.personService = personService;
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

    public List<String> seeAssistances(String personUsername) {
        Person person = personService.findByUsername(personUsername);
            if (person == null)
                throw new NotFoundException(Constants.INVALID_USERNAME);
            if (person instanceof Technician && !((Technician) person).isActive())
                throw new DeactivatedTechnicianException(Constants.DEACTIVATED_TECHNICIAN);
            return findAll().stream().map(Object::toString).toList();
    }
}
