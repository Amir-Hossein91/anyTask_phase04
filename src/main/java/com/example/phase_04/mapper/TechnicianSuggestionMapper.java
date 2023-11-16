package com.example.phase_04.mapper;

import com.example.phase_04.dto.request.TechnicianSuggestionRequestDTO;
import com.example.phase_04.dto.response.TechnicianSuggestionResponseDTO;
import com.example.phase_04.entity.TechnicianSuggestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface TechnicianSuggestionMapper {

    TechnicianSuggestionMapper INSTANCE = Mappers.getMapper(TechnicianSuggestionMapper.class);


    @Mapping(target = "order.id" , source = "requestDTO.orderId")
    TechnicianSuggestion dtoToModel (TechnicianSuggestionRequestDTO requestDTO);


    @Mapping(target = "technicianUsername", source = "technicianSuggestion.technician.username")
    @Mapping(target = "orderId", source = "technicianSuggestion.order.id")
    @Mapping(target = "technicianScore", source = "technicianSuggestion.technician.score")
    TechnicianSuggestionResponseDTO modelToDto (TechnicianSuggestion technicianSuggestion);
}
