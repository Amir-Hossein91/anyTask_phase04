package com.example.phase_04.service;

import com.example.phase_04.baseService.BaseService;
import com.example.phase_04.entity.Person;

public interface PersonService extends BaseService<Person> {

    Person findByUsername(String username);
}
