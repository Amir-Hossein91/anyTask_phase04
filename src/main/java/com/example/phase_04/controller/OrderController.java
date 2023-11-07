package com.example.phase_04.controller;

import com.example.phase_04.dto.request.OrderRequestDTO;
import com.example.phase_04.entity.Order;
import com.example.phase_04.mapper.OrderMapper;
import com.example.phase_04.service.impl.CustomerServiceImpl;
import com.example.phase_04.service.impl.OrderServiceImpl;
import com.example.phase_04.service.impl.SubAssistanceServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private OrderServiceImpl orderService;
    private SubAssistanceServiceImpl subAssistanceService;
    private CustomerServiceImpl customerService;

    public OrderController(OrderServiceImpl orderService,
                           SubAssistanceServiceImpl subAssistanceService,
                           CustomerServiceImpl customerService){
        this.orderService = orderService;
        this.subAssistanceService = subAssistanceService;
        this.customerService = customerService;
    }

    @PostMapping("/printModel")
    void printModel (@RequestBody OrderRequestDTO orderRequestDTO){
        Order order = OrderMapper.INSTANCE.dtoToModel(orderRequestDTO);
        order.setOrderRegistrationDateAndTime(LocalDateTime.now());

        System.out.println(OrderMapper.INSTANCE.modelToDto(order));
    }
}
