package com.example.phase_04.service.impl;

import com.example.phase_04.entity.*;
import com.example.phase_04.exceptions.NotFoundException;
import com.example.phase_04.repository.OrderRepository;
import com.example.phase_04.service.OrderService;
import com.example.phase_04.utility.Constants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;


    public OrderServiceImpl(OrderRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    @Transactional
    public Order saveOrUpdate(Order t) {
        return repository.save(t);
    }

    @Override
    @Transactional
    public void delete(Order t) {
        repository.delete(t);
    }

    @Override
    public Order findById(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("\nCould not find order with id = " + id));
    }

    @Override
    public List<Order> findAll() {
        return repository.findAll();
    }

    public List<Order> findRelatedOrders(Technician technician) {
        return repository.findRelatedOrders(technician).orElseThrow(
                () -> new NotFoundException(Constants.NO_RELATED_ORDERS)
        );
    }

    @Override
    @Transactional
    public void sendTechnicianSuggestion(Technician technician, Order order, TechnicianSuggestion technicianSuggestion) {
        List<Order> orders = repository.findRelatedOrders(technician).orElseThrow(
                () -> new NotFoundException(Constants.NO_RELATED_ORDERS)
        );
        boolean isFound = false;
        for (Order o : orders) {
            if (o.getId() == order.getId()) {
                isFound = true;
                break;
            }
        }
        if (!isFound)
            throw new NotFoundException(Constants.ORDER_IS_NOT_RELATED);
        if (technicianSuggestion != null) {

            if (technicianSuggestion.getTechSuggestedPrice() < order.getSubAssistance().getBasePrice())
                throw new IllegalArgumentException(Constants.INVALID_SUGGESTED_PRICE);

            if (technicianSuggestion.getTechSuggestedDate().isBefore(order.getOrderDescription().getCustomerDesiredDateAndTime()))
                throw new IllegalArgumentException(Constants.DATE_BEFORE_CUSTOMER_DESIRED);

            order.getTechnicianSuggestions().add(technicianSuggestion);
            saveOrUpdate(order);
        }
    }

    @Override
    public List<Order> findByCustomer(Customer customer) {
        return repository.findByCustomer(customer).orElseThrow(
                () -> new NotFoundException(Constants.NO_ORDERS_FOR_CUSTOMER)
        );
    }
}
