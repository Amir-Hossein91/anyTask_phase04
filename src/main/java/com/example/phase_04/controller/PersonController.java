package com.example.phase_04.controller;

import com.example.phase_04.controller.requestObjects.ChangePassword;
import com.example.phase_04.controller.requestObjects.Login;
import com.example.phase_04.dto.request.CustomerRequestDTO;
import com.example.phase_04.dto.request.ManagerRequestDTO;
import com.example.phase_04.dto.request.TechnicianRequestDTO;
import com.example.phase_04.dto.response.CustomerResponseDTO;
import com.example.phase_04.dto.response.ManagerResponseDTO;
import com.example.phase_04.dto.response.TechnicianResponseDTO;
import com.example.phase_04.entity.Customer;
import com.example.phase_04.entity.Manager;
import com.example.phase_04.entity.Person;
import com.example.phase_04.entity.Technician;
import com.example.phase_04.entity.enums.TechnicianStatus;
import com.example.phase_04.mapper.CustomerMapper;
import com.example.phase_04.mapper.ManagerMapper;
import com.example.phase_04.mapper.TechnicianMapper;
import com.example.phase_04.service.impl.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/person")
public class PersonController {
    public static int counter = 0;

    private final PersonServiceImpl personService;
    private final MailServiceImpl mailService;
    private final CustomerServiceImpl customerService;
    private final TechnicianServiceImpl technicianService;
    private final ManagerServiceImpl managerService;

    public PersonController(PersonServiceImpl personService,
                            MailServiceImpl mailService,
                            CustomerServiceImpl customerService,
                            TechnicianServiceImpl technicianService,
                            ManagerServiceImpl managerService) {
        this.personService = personService;
        this.mailService = mailService;
        this.customerService = customerService;
        this.technicianService = technicianService;
        this.managerService = managerService;
    }

    @PostMapping("/registerCustomer")
    public ResponseEntity<CustomerResponseDTO> saveCustomer(@RequestBody @Valid CustomerRequestDTO requestDTO) {
        Customer customer = CustomerMapper.INSTANCE.dtoToModel(requestDTO);
        customer.setRegistrationDate(LocalDateTime.now());
        personService.registerCustomer(customer);
        mailService.sendActivationLink(customer);
        return new ResponseEntity<>(CustomerMapper.INSTANCE.modelToDto(customerService.findById(customer.getId())), HttpStatus.CREATED);
    }

    @PostMapping("/registerManager")
    public ResponseEntity<ManagerResponseDTO> saveManager(@RequestBody @Valid
                                                          ManagerRequestDTO requestDTO) {
        Manager manager = ManagerMapper.INSTANCE.dtoToModel(requestDTO);
        manager.setRegistrationDate(LocalDateTime.now());
        personService.registerManager(manager);
        mailService.sendActivationLink(manager);
        return new ResponseEntity<>(ManagerMapper.INSTANCE.modelToDto(managerService.findById(manager.getId())), HttpStatus.CREATED);
    }

    @PostMapping(value = "/registerTechnician")
    public ResponseEntity<TechnicianResponseDTO> saveTechnician(@RequestBody @Valid TechnicianRequestDTO requestDTO) throws IOException {
        Technician technician = TechnicianMapper.INSTANCE.dtoToModel(requestDTO);
        technician.setRegistrationDate(LocalDateTime.now());

        technician.setTechnicianStatus(TechnicianStatus.NEW);

        personService.registerTechnician(technician);
        mailService.sendActivationLink(technician);
        byte[] image = technician.getImage();
        Path path = Path.of("C:\\Users\\AmirHossein\\IdeaProjects\\anyTask\\image_output\\technician_" + (++counter) + ".jpg");
        Files.write(path, image);

        return new ResponseEntity<>(TechnicianMapper.INSTANCE.modelToDto(technicianService.findById(technician.getId())), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<String> login(@RequestBody Login request) {
        Person person = personService.login(request.getUsername(), request.getPassword());
        return new ResponseEntity<>("Hello " + person.getFirstName() + " " + person.getLastName() +
                "!\nYou signed in as a " + person.getClass().getSimpleName() + ".", HttpStatus.OK);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePassword request) {
        personService.changePassword(request.getUsername(), request.getCurrentPassword(), request.getNewPassword());

        return new ResponseEntity<>("Password changed successfully!", HttpStatus.CREATED);
    }

    @GetMapping("verifyEmailAddress/{username}")
    public ResponseEntity<String> verifyEmailAddress(@PathVariable String username) {
        personService.enablePerson(username);
        Person person = personService.findByUsername(username);
        if (person instanceof Technician) {
            return new ResponseEntity<>("Your account is enabled successfully!\nPlease wait for manager to check your information", HttpStatus.OK);
        } else
            return new ResponseEntity<>("Your account is enabled successfully!", HttpStatus.OK);
    }
}
