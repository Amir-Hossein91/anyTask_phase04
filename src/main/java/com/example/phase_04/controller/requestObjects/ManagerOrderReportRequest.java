package com.example.phase_04.controller.requestObjects;

import com.example.phase_04.entity.enums.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ManagerOrderReportRequest {
    private long customerId;
    private long technicianId;
    private LocalDateTime from;
    private LocalDateTime until;
    private OrderStatus status;
    private String assistanceTitle;
    private String subAssistanceTitle;
}
