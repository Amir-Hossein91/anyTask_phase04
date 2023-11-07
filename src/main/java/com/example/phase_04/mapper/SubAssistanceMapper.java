package com.example.phase_04.mapper;

import com.example.phase_04.dto.request.SubAssistanceRequestDTO;
import com.example.phase_04.dto.response.SubAssistanceResponseDTO;
import com.example.phase_04.entity.SubAssistance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface SubAssistanceMapper {

    SubAssistanceMapper INSTANCE = Mappers.getMapper(SubAssistanceMapper.class);

    @Mapping(target = "assistance.title" , source = "requestDTO.assistanceTitle")
    SubAssistance dtoToModel (SubAssistanceRequestDTO requestDTO);

    @Mapping(target = "assistanceTitle" , source = "subAssistance.assistance.title")
    SubAssistanceResponseDTO modelToDto (SubAssistance subAssistance);
}
