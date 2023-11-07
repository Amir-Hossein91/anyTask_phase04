package com.example.phase_04.controller.requestObjects;

import lombok.Getter;

@Getter
public class MarkAsStartedOrFinished {

    private String customerUsername;
    private long orderId;
}
