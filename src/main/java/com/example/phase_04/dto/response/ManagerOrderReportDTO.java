package com.example.phase_04.dto.response;

import com.example.phase_04.entity.enums.OrderStatus;

import java.time.LocalDateTime;

public record ManagerOrderReportDTO(long id,
                                    LocalDateTime orderRegistrationDateAndTime,
                                    String subAssistanceTitle,
                                    long customerId,
                                    LocalDateTime customerDesiredDateAndTime,
                                    String taskDetails,
                                    String address,
                                    long customerSuggestedPrice,
                                    long technicianId,
                                    long acceptedPrice,
                                    LocalDateTime acceptedDate,
                                    int estimatedTaskDuration,
                                    OrderStatus orderStatus,
                                    LocalDateTime taskStartDate,
                                    LocalDateTime taskFinishDate,
                                    int technicianScore) {
}
