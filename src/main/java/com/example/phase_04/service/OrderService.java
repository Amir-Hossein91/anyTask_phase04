package com.example.phase_04.service;

import com.example.phase_04.baseService.BaseService;
import com.example.phase_04.entity.Customer;
import com.example.phase_04.entity.Order;
import com.example.phase_04.entity.Technician;
import com.example.phase_04.entity.TechnicianSuggestion;

import java.util.List;

public interface OrderService extends BaseService<Order> {

    List<Order> findRelatedOrders(Technician technician);
    void sendTechnicianSuggestion(Technician technician, Order order, TechnicianSuggestion technicianSuggestion);
    List<Order> findByCustomer(Customer customer);
}
