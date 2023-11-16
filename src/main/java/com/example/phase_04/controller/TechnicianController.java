package com.example.phase_04.controller;

import com.example.phase_04.controller.requestObjects.CustomerOrTechnicianFilterOrders;
import com.example.phase_04.controller.requestObjects.SeeScore;
import com.example.phase_04.dto.request.TechnicianSuggestionRequestDTO;
import com.example.phase_04.dto.response.OrderResponseDTO;
import com.example.phase_04.dto.response.SubAssistanceResponseDTO;
import com.example.phase_04.dto.response.TechnicianSuggestionResponseDTO;
import com.example.phase_04.entity.Order;
import com.example.phase_04.entity.SubAssistance;
import com.example.phase_04.entity.TechnicianSuggestion;
import com.example.phase_04.entity.enums.OrderStatus;
import com.example.phase_04.mapper.OrderMapper;
import com.example.phase_04.mapper.SubAssistanceMapper;
import com.example.phase_04.mapper.TechnicianSuggestionMapper;
import com.example.phase_04.service.impl.OrderServiceImpl;
import com.example.phase_04.service.impl.SubAssistanceServiceImpl;
import com.example.phase_04.service.impl.TechnicianServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/technician")
public class TechnicianController {

    private final TechnicianServiceImpl technicianService;
    private final OrderServiceImpl orderService;
    private final SubAssistanceServiceImpl subAssistanceService;

    public TechnicianController (TechnicianServiceImpl technicianService,
                                 OrderServiceImpl orderService,
                                 SubAssistanceServiceImpl subAssistanceService){
        this.technicianService = technicianService;
        this.orderService = orderService;
        this.subAssistanceService = subAssistanceService;
    }

    @GetMapping("/relatedOrders")
    @Transactional
    public ResponseEntity<List<OrderResponseDTO>> seeRelatedOrders (){
        List<Order> relatedOrders = technicianService.findRelatedOrders();
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
        technicianSuggestion.setOrder(orderService.findById(requestDTO.orderId()));
        technicianSuggestion.setDateAndTimeOfTechSuggestion(LocalDateTime.now());

        technicianService.sendTechnicianSuggestion(requestDTO.orderId(), technicianSuggestion);

        return new ResponseEntity<>(TechnicianSuggestionMapper.INSTANCE.modelToDto(technicianSuggestion),HttpStatus.CREATED);
    }

    @GetMapping("/seeSubAssistance")
    public ResponseEntity<List<SubAssistanceResponseDTO>> seeSubAssistances(){
        List<SubAssistance> subAssistances = subAssistanceService.showSubAssistancesToOthers();
        List<SubAssistanceResponseDTO> responseDTOS = new ArrayList<>();

        for(SubAssistance s: subAssistances)
            responseDTOS.add(SubAssistanceMapper.INSTANCE.modelToDto(s));

        return new ResponseEntity<>(responseDTOS,HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/seeScore")
    public ResponseEntity<String> seeScore (@RequestBody SeeScore request){
        int score = technicianService.seeTechnicianScore(request.getOrderId());
        return new ResponseEntity<>("Technician score: " + score , HttpStatus.OK);
    }

    @PostMapping("/filterOrders")
    public ResponseEntity<List<OrderResponseDTO>> filterOrders (@RequestBody CustomerOrTechnicianFilterOrders request){
        Optional<OrderStatus> orderStatus = Optional.ofNullable(request.getOrderStatus());
        List<Order> orders = orderService.customerOrTechnicianFilterOrders(orderStatus);
        List<OrderResponseDTO> orderDTOs = new ArrayList<>();
        for(Order o : orders)
            orderDTOs.add(OrderMapper.INSTANCE.modelToDto(o));
        return new ResponseEntity<>(orderDTOs,HttpStatus.OK);
    }

    @GetMapping("/seeCredit")
    @Transactional
    public ResponseEntity<String> seeCredit (){
        String creditReport = technicianService.reportCredit();
        return new ResponseEntity<>(creditReport,HttpStatus.OK);
    }
}
