package com.example.phase_04.mapper;

import com.example.phase_04.dto.request.AssistanceRequestDTO;
import com.example.phase_04.dto.response.AssistanceResponseDTO;
import com.example.phase_04.entity.Assistance;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface AssistanceMapper {

    AssistanceMapper INSTANCE = Mappers.getMapper(AssistanceMapper.class);

    Assistance dtoToModel (AssistanceRequestDTO requestDTO);

    AssistanceResponseDTO modelToDto (Assistance assistance);

}
