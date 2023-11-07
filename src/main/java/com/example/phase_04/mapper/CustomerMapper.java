package com.example.phase_04.mapper;

import com.example.phase_04.dto.request.CustomerRequestDTO;
import com.example.phase_04.dto.response.CustomerResponseDTO;
import com.example.phase_04.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface CustomerMapper {

    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    Customer dtoToModel (CustomerRequestDTO requestDTO);

    CustomerResponseDTO modelToDto (Customer customer);
}
