package com.example.phase_04.service;

import com.example.phase_04.baseService.BaseService;
import com.example.phase_04.entity.Customer;

public interface CustomerService extends BaseService<Customer> {
    Customer findByUsername (String customerUsername);
}
