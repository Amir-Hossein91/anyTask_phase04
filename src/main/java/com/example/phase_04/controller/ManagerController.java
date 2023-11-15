package com.example.phase_04.controller;

import com.example.phase_04.controller.requestObjects.*;
import com.example.phase_04.dto.request.AssistanceRequestDTO;
import com.example.phase_04.dto.request.SubAssistanceRequestDTO;
import com.example.phase_04.dto.response.*;
import com.example.phase_04.entity.*;
import com.example.phase_04.entity.enums.OrderStatus;
import com.example.phase_04.entity.enums.TechnicianStatus;
import com.example.phase_04.mapper.*;
import com.example.phase_04.service.impl.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/manager")
public class ManagerController {

    private final PersonServiceImpl personService;
    private final AssistanceServiceImpl assistanceService;
    private final SubAssistanceServiceImpl subAssistanceService;
    private final TechnicianServiceImpl technicianService;
    private final OrderServiceImpl orderService;

    public ManagerController(PersonServiceImpl personService,
                             AssistanceServiceImpl assistanceService,
                             SubAssistanceServiceImpl subAssistanceService,
                             TechnicianServiceImpl technicianService,
                             OrderServiceImpl orderService) {
        this.personService = personService;
        this.assistanceService = assistanceService;
        this.subAssistanceService = subAssistanceService;
        this.technicianService = technicianService;
        this.orderService = orderService;
    }

    @PostMapping("/addAssistance")
    public ResponseEntity<AssistanceResponseDTO> addAssistance(@RequestBody @Valid
                                                               AssistanceRequestDTO requestDTO) {
        Assistance assistance = AssistanceMapper.INSTANCE.dtoToModel(requestDTO);
        return new ResponseEntity<>(AssistanceMapper.INSTANCE.modelToDto(assistanceService.addAssistance(assistance)), HttpStatus.CREATED);
    }

    @PostMapping("/addSubAssistance")
    public ResponseEntity<SubAssistanceResponseDTO> addSubAssistance(@RequestBody @Valid
                                                                     SubAssistanceRequestDTO requestDTO) {
        SubAssistance subAssistance = SubAssistanceMapper.INSTANCE.dtoToModel(requestDTO);
        return new ResponseEntity<>(SubAssistanceMapper.INSTANCE
                .modelToDto(subAssistanceService.addSubAssistance(subAssistance, subAssistance.getAssistance().getTitle())), HttpStatus.CREATED);
    }

    @GetMapping("/unapprovedTechnicians")
    public ResponseEntity<List<TechnicianResponseDTO>> seeNewTechnicians() {

        List<Technician> technicians = technicianService.seeUnapprovedTechnicians();
        List<TechnicianResponseDTO> responseDTOS = new ArrayList<>();

        boolean isListChanged = false;

        for (Technician t : technicians) {
            responseDTOS.add(TechnicianMapper.INSTANCE.modelToDto(t));
            if (t.getTechnicianStatus() == TechnicianStatus.NEW) {
                t.setTechnicianStatus(TechnicianStatus.PENDING);
                isListChanged = true;
            }
        }
        if (isListChanged)
            technicianService.saveOrUpdate(technicians);
        return new ResponseEntity<>(responseDTOS, HttpStatus.OK);
    }

    @PostMapping("/assign")
    public ResponseEntity<String> assignTechnicianToSubAssistance(@RequestBody AssignTechnician request) {

        String techUsername = request.getTechnicianUsername();
        String subAssistanceTitle = request.getSubAssistanceTitle();
        String assistanceTitle = request.getAssistanceTitle();

        technicianService.addTechnicianToSubAssistance(techUsername, subAssistanceTitle, assistanceTitle);

        return new ResponseEntity<>("Technician assigned successfully", HttpStatus.CREATED);
    }

    @PostMapping("/resign")
    public ResponseEntity<String> removeTechnicianFromSubAssistance(@RequestBody AssignTechnician request) {

        String techUsername = request.getTechnicianUsername();
        String subAssistanceTitle = request.getSubAssistanceTitle();
        String assistanceTitle = request.getAssistanceTitle();

        technicianService.removeTechnicianFromSubAssistance(techUsername, subAssistanceTitle, assistanceTitle);

        return new ResponseEntity<>("Technician resigned successfully", HttpStatus.CREATED);
    }

    @GetMapping("/getSubAssistances")
    @Transactional
    public ResponseEntity<List<SubAssistanceResponseForManagerDTO>> getSubAssistances() {
        List<SubAssistance> subAssistances = subAssistanceService.showSubAssistancesToManager();

        Map<SubAssistanceResponseDTO, List<TechnicianResponseDTO>> resultsMap = new HashMap<>();
        for (SubAssistance s : subAssistances) {
            SubAssistanceResponseDTO key = SubAssistanceMapper.INSTANCE.modelToDto(s);
            List<TechnicianResponseDTO> value = new ArrayList<>();
            for (Technician t : s.getTechnicians()) {
                TechnicianResponseDTO responseDTO = TechnicianMapper.INSTANCE.modelToDto(t);
                value.add(responseDTO);
            }
            resultsMap.put(key, value);
        }

        List<SubAssistanceResponseForManagerDTO> result = new ArrayList<>();
        for (Map.Entry<SubAssistanceResponseDTO, List<TechnicianResponseDTO>> e : resultsMap.entrySet()) {
            result.add(SubAssistanceResponseForManagerDTO.builder()
                    .id(e.getKey().id())
                    .title(e.getKey().title())
                    .basePrice(e.getKey().basePrice())
                    .assistanceTitle(e.getKey().assistanceTitle())
                    .about(e.getKey().about())
                    .technicians(e.getValue())
                    .build());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/changeBasePrice")
    public ResponseEntity<SubAssistanceResponseDTO> changeBasePrice(@RequestBody ChangeBasePrice request) {

        String subAssistanceTitle = request.getSubAssistanceTitle();
        String assistanceTitle = request.getAssistanceTitle();
        long basePrice = request.getNewBasePrice();

        subAssistanceService.changeBasePrice(subAssistanceTitle, assistanceTitle, basePrice);

        SubAssistanceResponseDTO responseDTO = SubAssistanceMapper.INSTANCE
                .modelToDto(subAssistanceService.findSubAssistance(subAssistanceTitle, assistanceService.findAssistance(assistanceTitle)));

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/changeDescription")
    public ResponseEntity<SubAssistanceResponseDTO> changeDescription(@RequestBody ChangeDescription request) {

        String subAssistanceTitle = request.getSubAssistanceTitle();
        String assistanceTitle = request.getAssistanceTitle();
        String description = request.getNewDescription();

        subAssistanceService.changeDescription(subAssistanceTitle, assistanceTitle, description);

        SubAssistanceResponseDTO responseDTO = SubAssistanceMapper.INSTANCE
                .modelToDto(subAssistanceService.findSubAssistance(subAssistanceTitle, assistanceService.findAssistance(assistanceTitle)));

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/getDeactivated")
    @Transactional
    public ResponseEntity<List<TechnicianResponseDTO>> findDeactivatedTechnicians() {
        List<Technician> deactivatedList = technicianService.seeDeactivatedTechnicians();
        List<TechnicianResponseDTO> responseDTOS = new ArrayList<>();

        for (Technician t : deactivatedList)
            responseDTOS.add(TechnicianMapper.INSTANCE.modelToDto(t));

        return new ResponseEntity<>(responseDTOS, HttpStatus.OK);
    }

    @PostMapping("/filter")
    @Transactional
    public ResponseEntity<List<PersonResponseDTO>> filter(@RequestBody @Valid Filter request) {
        if (request.getMaxMin() != null && !(request.getMaxMin().equals("max") || request.getMaxMin().equals("min")))
            throw new IllegalArgumentException("'maxMin' field can only be 'max' or 'min'");

        if (request.getRole() != null && !(request.getRole().equals("technician") || request.getRole().equals("customer")))
            throw new IllegalArgumentException("'roll' field can only be 'technician' or 'customer'");

        Optional<String> role = Optional.ofNullable(request.getRole());
        Optional<String> firstName = Optional.ofNullable(request.getFirstName());
        Optional<String> lastname = Optional.ofNullable(request.getLastname());
        Optional<String> email = Optional.ofNullable(request.getEmail());
        long subAssistanceId = 0;
        if (request.getSubAsssitanceTitle() != null && request.getAssitanceTitle() != null) {
            String subTitle = request.getSubAsssitanceTitle();
            Assistance assistance = assistanceService.findAssistance(request.getAssitanceTitle());
            if (assistance != null) {
                SubAssistance subAssistance = subAssistanceService.findSubAssistance(subTitle, assistance);
                if (subAssistance != null) {
                    subAssistanceId = subAssistance.getId();
                }
            }
        }
        Optional<String> maxMin = Optional.ofNullable(request.getMaxMin());
        Optional<LocalDateTime> registeredFrom = Optional.ofNullable(request.getRegisteredFrom());
        Optional<LocalDateTime> registeredUntil = Optional.ofNullable(request.getRegisteredUntil());
        Optional<Integer> minOrders = Optional.ofNullable(request.getMinOrders());
        Optional<Integer> maxOrders = Optional.ofNullable(request.getMaxOrders());

        List<Person> result = personService.filter(role, firstName, lastname, email, subAssistanceId, maxMin,registeredFrom,registeredUntil,minOrders,maxOrders);
        List<PersonResponseDTO> responseDTOS = new ArrayList<>();

        for (Person p : result)
            responseDTOS.add(PersonMapper.INSTANCE.modelToDto(p));

        return new ResponseEntity<>(responseDTOS, HttpStatus.OK);
    }

    @PostMapping("filterOrders")
    public ResponseEntity<List<ManagerOrderReportDTO>> getOrderReport(@RequestBody ManagerOrderReportRequest request){
        Optional<LocalDateTime> from = Optional.ofNullable(request.getFrom());
        Optional<LocalDateTime> until = Optional.ofNullable(request.getUntil());
        Optional<OrderStatus> status = Optional.ofNullable(request.getStatus());
        Optional<String> assistanceTitle = Optional.ofNullable(request.getAssistanceTitle());
        Optional<String> subAssistanceTitle = Optional.ofNullable(request.getSubAssistanceTitle());

        List<Order> orders = orderService.managerFilterOrders(request.getCustomerId(), request.getTechnicianId(), from, until, status, assistanceTitle,subAssistanceTitle);
        List<ManagerOrderReportDTO> reportDTOS = new ArrayList<>();
        for(Order o: orders){
            reportDTOS.add(OrderMapper.INSTANCE.modelToReport(o));
        }


        return new ResponseEntity<>(reportDTOS,HttpStatus.OK);
    }

}
