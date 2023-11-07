package com.example.phase_04.dto.request;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

public record SubAssistanceRequestDTO(
                                      @NotNull(message = "Sub-assistance title can not be null")
                                      String title,
                                      @Range(min = 0, message = "Base price can not be negative")
                                      long basePrice,
                                      String assistanceTitle,
                                      @NotNull(message = "Sub-assistance should have some descriptions")
                                      String about) {
}
