package com.example.phase_04.service;

import com.example.phase_04.baseService.BaseService;
import com.example.phase_04.entity.Technician;

import java.util.List;

public interface TechnicianService extends BaseService<Technician> {

    Technician findByUsername (String technicianUsername);

    List<Technician> saveOrUpdate(List<Technician> technicians);
}
