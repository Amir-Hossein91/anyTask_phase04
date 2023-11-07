package com.example.phase_04.dto.response;

import java.time.LocalDateTime;

public record ManagerResponseDTO(long id,
                                 String firstName,
                                 String lastName,
                                 String email,
                                 String username,
                                 LocalDateTime registrationDate) {
}
