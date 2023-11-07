package com.example.phase_04.controller;

import com.example.phase_04.controller.requestObjects.ChangePassword;
import com.example.phase_04.controller.requestObjects.Login;
import com.example.phase_04.entity.Person;
import com.example.phase_04.service.impl.PersonServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/person")
public class PersonController {

    private final PersonServiceImpl personService;

    public PersonController(PersonServiceImpl personService) {
        this.personService = personService;
    }

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<String> login(@RequestBody Login request){
        Person person = personService.login(request.getUsername(), request.getPassword());
        return new ResponseEntity<>("Hello " + person.getFirstName() + " " + person.getLastName() +
                "!\nYou signed in as a " + person.getClass().getSimpleName() + ".", HttpStatus.OK);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePassword request){
        personService.changePassword(request.getUsername(), request.getCurrentPassword(), request.getNewPassword());

        return new ResponseEntity<>("Password changed successfully!",HttpStatus.CREATED);
    }
}
