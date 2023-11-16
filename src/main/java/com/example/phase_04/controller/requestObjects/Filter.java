package com.example.phase_04.controller.requestObjects;

import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

@Getter
public class Filter {
    private String role;
    private String firstName;
    private String lastname;
    private String email;
    private String subAssistanceTitle;
    private String assistanceTitle;
    private String maxMin;
    private LocalDateTime registeredFrom;
    private LocalDateTime registeredUntil;
    @Range(min = 0, message = "Number of orders can not be negative")
    private Integer minOrders;
    @Range(min = 0, message = "Number of orders can not be negative")
    private Integer maxOrders;
}
