package com.example.phase_04.controller;


import com.example.phase_04.dto.request.AssistanceRequestDTO;
import com.example.phase_04.dto.response.AssistanceResponseDTO;
import com.example.phase_04.entity.Assistance;
import com.example.phase_04.mapper.AssistanceMapper;
import com.example.phase_04.service.impl.AssistanceServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assistance")
public class AssistanceController {

    private final AssistanceServiceImpl assistanceService;

    public AssistanceController(AssistanceServiceImpl assistanceService) {
        this.assistanceService = assistanceService;
    }

    @PostMapping("/save")
    public ResponseEntity<AssistanceResponseDTO> saveAssistance (
            @RequestBody @Valid AssistanceRequestDTO requestDTO){
        Assistance assistance = AssistanceMapper.INSTANCE.dtoToModel(requestDTO);
        return new ResponseEntity<>(AssistanceMapper.INSTANCE.modelToDto(assistanceService.saveOrUpdate(assistance)), HttpStatus.CREATED);
    }
}
