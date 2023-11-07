package com.example.phase_04.mapper;

import com.example.phase_04.dto.request.OrderDescriptionRequestDTO;
import com.example.phase_04.dto.response.OrderDescriptionResponseDTO;
import com.example.phase_04.entity.OrderDescription;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface OrderDescriptionMapper {

    OrderDescriptionMapper INSTANCE = Mappers.getMapper(OrderDescriptionMapper.class);

    OrderDescription dtoToModel (OrderDescriptionRequestDTO requestDTO);

    OrderDescriptionResponseDTO modelToDto (OrderDescription orderDescription);
}
