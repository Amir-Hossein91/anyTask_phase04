package com.example.phase_04.service.impl;

import com.example.phase_04.entity.Manager;
import com.example.phase_04.exceptions.NotFoundException;
import com.example.phase_04.repository.ManagerRepository;
import com.example.phase_04.service.ManagerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ManagerServiceImpl implements ManagerService {

    private final ManagerRepository repository;

    public ManagerServiceImpl(ManagerRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    @Transactional
    public Manager saveOrUpdate(Manager t) {
        return repository.save(t);
    }

    @Override
    @Transactional
    public void delete(Manager t) {
        repository.delete(t);
    }

    @Override
    public Manager findById(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("\nCould not find manager with id = " + id));
    }

    @Override
    public List<Manager> findAll() {
            return repository.findAll();
    }

    public boolean doesManagerExist() {
        return !repository.findAll().isEmpty();
    }

    @Override
    public Manager findByUsername(String managerUsername) {
        return repository.findByUsername(managerUsername).orElse(null);
    }
}
