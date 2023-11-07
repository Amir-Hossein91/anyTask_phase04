package com.example.phase_04.controller;

import com.example.phase_04.controller.requestObjects.SeeScore;
import com.example.phase_04.dto.request.TechnicianRequestDTO;
import com.example.phase_04.dto.request.TechnicianSuggestionRequestDTO;
import com.example.phase_04.dto.response.OrderResponseDTO;
import com.example.phase_04.dto.response.SubAssistanceResponseDTO;
import com.example.phase_04.dto.response.TechnicianResponseDTO;
import com.example.phase_04.dto.response.TechnicianSuggestionResponseDTO;
import com.example.phase_04.entity.Order;
import com.example.phase_04.entity.SubAssistance;
import com.example.phase_04.entity.Technician;
import com.example.phase_04.entity.TechnicianSuggestion;
import com.example.phase_04.entity.enums.TechnicianStatus;
import com.example.phase_04.mapper.OrderMapper;
import com.example.phase_04.mapper.SubAssistanceMapper;
import com.example.phase_04.mapper.TechnicianMapper;
import com.example.phase_04.mapper.TechnicianSuggestionMapper;
import com.example.phase_04.service.impl.OrderServiceImpl;
import com.example.phase_04.service.impl.PersonServiceImpl;
import com.example.phase_04.service.impl.SubAssistanceServiceImpl;
import com.example.phase_04.service.impl.TechnicianServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/technician")
public class TechnicianController {
    public static int counter = 0;

    private final TechnicianServiceImpl technicianService;
    private final PersonServiceImpl personService;
    private final OrderServiceImpl orderService;
    private final SubAssistanceServiceImpl subAssistanceService;

    public TechnicianController (TechnicianServiceImpl technicianService,
                                 PersonServiceImpl personService,
                                 OrderServiceImpl orderService,
                                 SubAssistanceServiceImpl subAssistanceService){
        this.technicianService = technicianService;
        this.personService = personService;
        this.orderService = orderService;
        this.subAssistanceService = subAssistanceService;
    }

    @PostMapping(value = "/register")
    public ResponseEntity<TechnicianResponseDTO> saveTechnician (@RequestBody @Valid TechnicianRequestDTO requestDTO) throws IOException {
        Technician technician = TechnicianMapper.INSTANCE.dtoToModel(requestDTO);
        technician.setRegistrationDate(LocalDateTime.now());

        technician.setTechnicianStatus(TechnicianStatus.NEW);

        personService.registerTechnician(technician);

        byte[] image = technician.getImage();
        Path path = Path.of("C:\\Users\\AmirHossein\\IdeaProjects\\anyTask\\image_output\\technician_"+(++counter)+".jpg");
        Files.write(path,image);

        return new ResponseEntity<>(TechnicianMapper.INSTANCE.modelToDto(technician), HttpStatus.CREATED);
    }

    @GetMapping("/relatedOrders/{username}")
    @Transactional
    public ResponseEntity<List<OrderResponseDTO>> seeRelatedOrders (@PathVariable String username){
        List<Order> relatedOrders = technicianService.findRelatedOrders(username);
        List<OrderResponseDTO> responseDTOS = new ArrayList<>();

        for(Order o : relatedOrders)
            responseDTOS.add(OrderMapper.INSTANCE.modelToDto(o));
        return new ResponseEntity<>(responseDTOS,HttpStatus.OK);
    }

    @PostMapping("/sendSuggestion")
    @Transactional
    public ResponseEntity<TechnicianSuggestionResponseDTO> sendTechnicianSuggestion (@RequestBody @Valid
                                                                                     TechnicianSuggestionRequestDTO requestDTO){
        TechnicianSuggestion technicianSuggestion = TechnicianSuggestionMapper.INSTANCE.dtoToModel(requestDTO);
        technicianSuggestion.setTechnician(technicianService.findByUsername(requestDTO.technicianUsername()));
        technicianSuggestion.setOrder(orderService.findById(requestDTO.orderId()));
        technicianSuggestion.setDateAndTimeOfTechSuggestion(LocalDateTime.now());

        technicianService.sendTechnicianSuggestion(requestDTO.technicianUsername(), requestDTO.orderId(), technicianSuggestion);

        return new ResponseEntity<>(TechnicianSuggestionMapper.INSTANCE.modelToDto(technicianSuggestion),HttpStatus.CREATED);
    }

    @GetMapping("/seeSubAssistance/{username}")
    public ResponseEntity<List<SubAssistanceResponseDTO>> seeSubAssistances(@PathVariable String username){
        List<SubAssistance> subAssistances = subAssistanceService.showSubAssistancesToOthers(username);
        List<SubAssistanceResponseDTO> responseDTOS = new ArrayList<>();

        for(SubAssistance s: subAssistances)
            responseDTOS.add(SubAssistanceMapper.INSTANCE.modelToDto(s));

        return new ResponseEntity<>(responseDTOS,HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/seeScore")
    public ResponseEntity<String> seeScore (@RequestBody SeeScore request){
        int score = technicianService.seeTechnicianScore(request.getTechnicianUsername(), request.getOrderId());
        return new ResponseEntity<>("Technician score: " + score , HttpStatus.OK);
    }
}
