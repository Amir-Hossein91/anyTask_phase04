package com.example.phase_04.mapper;

import com.example.phase_04.dto.request.PersonRequestDTO;
import com.example.phase_04.dto.response.PersonResponseDTO;
import com.example.phase_04.entity.Person;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    Person dtoToModel (PersonRequestDTO requestDTO);

    PersonResponseDTO modelToDto (Person person);
}
