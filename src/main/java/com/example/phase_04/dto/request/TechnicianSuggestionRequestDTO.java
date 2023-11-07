package com.example.phase_04.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

public record TechnicianSuggestionRequestDTO (String technicianUsername,
                                              long orderId,
                                              @Range(min = 0, message = "Price can not be negative")
                                              long techSuggestedPrice,
                                              @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
                                              @NotNull(message = "A technician suggested start date must be set")
                                              LocalDateTime techSuggestedDate,
                                              @Range(min = 0, message = "Task duration can not be negative")
                                              int taskEstimatedDuration) {
}
