package com.example.phase_04.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

public record SubAssistanceRequestDTO(
                                      @NotBlank(message = "Sub-assistance title can not be blank")
                                      String title,
                                      @Range(min = 0, message = "Base price can not be negative")
                                      long basePrice,
                                      String assistanceTitle,
                                      @NotBlank(message = "Sub-assistance should have some descriptions")
                                      String about) {
}
