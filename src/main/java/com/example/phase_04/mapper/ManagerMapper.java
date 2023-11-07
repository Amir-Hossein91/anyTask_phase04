package com.example.phase_04.mapper;

import com.example.phase_04.dto.request.ManagerRequestDTO;
import com.example.phase_04.dto.response.ManagerResponseDTO;
import com.example.phase_04.entity.Manager;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface ManagerMapper {

    ManagerMapper INSTANCE = Mappers.getMapper(ManagerMapper.class);

    Manager dtoToModel(ManagerRequestDTO requestDTO);

    ManagerResponseDTO modelToDto(Manager manager);
}
