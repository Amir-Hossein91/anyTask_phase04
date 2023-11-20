package com.example.phase_04.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

public record OrderDescriptionRequestDTO(
                                        @Range(min = 0, message = "Price can not be negative")
                                        long customerSuggestedPrice,
                                        @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
                                        @NotNull(message = "Customer desired start date must be set")
                                        LocalDateTime customerDesiredDateAndTime,
                                        @NotBlank(message = "Brief descriptions of task should be submitted")
                                        String taskDetails,
                                        @NotBlank(message = "Address can not be blank")
                                        String address) {
}
