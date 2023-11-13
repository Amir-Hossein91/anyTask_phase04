package com.example.phase_04.dto.response;

import com.example.phase_04.entity.enums.Role;

import java.time.LocalDateTime;

public record PersonResponseDTO(long id,
                                String firstName,
                                String lastName,
                                String email,
                                String username,
                                Role role,
                                LocalDateTime registrationDate,
                                int orderCount) {
}
