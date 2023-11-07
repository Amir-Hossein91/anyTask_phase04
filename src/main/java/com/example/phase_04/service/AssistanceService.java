package com.example.phase_04.service;

import com.example.phase_04.baseService.BaseService;
import com.example.phase_04.entity.Assistance;


public interface AssistanceService extends BaseService<Assistance> {

    Assistance findAssistance(String assistanceName);
}
