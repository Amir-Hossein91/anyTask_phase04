package com.example.phase_04.service;

import com.example.phase_04.baseService.BaseService;
import com.example.phase_04.entity.Assistance;
import com.example.phase_04.entity.SubAssistance;
import com.example.phase_04.entity.Technician;

import java.util.List;

public interface SubAssistanceService extends BaseService<SubAssistance> {

    SubAssistance findSubAssistance(String title, Assistance assistance);

   List<SubAssistance> findByTechniciansContaining(Technician technician);
}
