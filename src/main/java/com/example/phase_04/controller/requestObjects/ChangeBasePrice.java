package com.example.phase_04.controller.requestObjects;

import lombok.Data;

@Data
public class ChangeBasePrice {
    private String subAssistanceTitle;
    private String assistanceTitle;
    private Long newBasePrice;
}
