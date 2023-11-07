package com.example.phase_04.service;

import com.example.phase_04.baseService.BaseService;
import com.example.phase_04.entity.Manager;


public interface ManagerService extends BaseService<Manager> {

    boolean doesManagerExist();
    Manager findByUsername(String managerUsername);
}
