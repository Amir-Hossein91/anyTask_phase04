package com.example.phase_04.dto.response;

import com.example.phase_04.entity.enums.OrderStatus;

import java.time.LocalDateTime;

public record OrderResponseDTO(long id,
                               String subAssistanceTitle,
                               long customerId,
                               long technicianId,
                               LocalDateTime orderRegistrationDateAndTime,
                               long customerSuggestedPrice,
                               LocalDateTime customerDesiredDateAndTime,
                               String taskDetails,
                               String address,
                               OrderStatus orderStatus,
                               int technicianScore) {
}
