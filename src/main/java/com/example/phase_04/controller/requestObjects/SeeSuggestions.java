package com.example.phase_04.controller.requestObjects;

import lombok.Getter;

@Getter
public class SeeSuggestions {
    private String customerUsername;
    private long orderId;
    private String orderingBy;
}
