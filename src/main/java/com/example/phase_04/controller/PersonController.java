package com.example.phase_04.controller;

import com.example.phase_04.controller.requestObjects.ChangePassword;
import com.example.phase_04.controller.requestObjects.Login;
import com.example.phase_04.entity.Person;
import com.example.phase_04.service.impl.MailServiceImpl;
import com.example.phase_04.service.impl.PersonServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/person")
public class PersonController {

    private final PersonServiceImpl personService;
    private final MailServiceImpl mailService;

    public PersonController(PersonServiceImpl personService,
                            MailServiceImpl mailService) {
        this.personService = personService;
        this.mailService = mailService;
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

    @GetMapping("sendEmail")
    public ResponseEntity<String> sendEmail(){
        String to = "amir.ahmadi9034@gmail.com";
        String subject = "TEST EMAIL";
        String text = "This email is only a test";
        mailService.sendSimpleMessage(to,subject,text);
        return new ResponseEntity<>("Email sent successfully",HttpStatus.OK);
    }
}
