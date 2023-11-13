package com.example.phase_04.controller.requestObjects;

import com.example.phase_04.entity.enums.OrderStatus;
import lombok.Getter;

@Getter
public class CustomerOrTechnicianFilterOrders {
    private OrderStatus orderStatus;
}
