package com.example.phase_04.service.impl;

import com.example.phase_04.entity.OrderDescription;
import com.example.phase_04.exceptions.NotFoundException;
import com.example.phase_04.repository.OrderDescriptionRepository;
import com.example.phase_04.service.OrderDescriptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderDescriptionServiceImpl implements OrderDescriptionService {

    private final OrderDescriptionRepository repository;

    public OrderDescriptionServiceImpl(OrderDescriptionRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    @Transactional
    public OrderDescription saveOrUpdate(OrderDescription t) {
            return repository.save(t);
    }

    @Override
    @Transactional
    public void delete(OrderDescription t) {
            repository.delete(t);
    }

    @Override
    public OrderDescription findById(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("\nCould not find order description with id = " + id));
    }

    @Override
    public List<OrderDescription> findAll() {
        return repository.findAll();
    }
}
