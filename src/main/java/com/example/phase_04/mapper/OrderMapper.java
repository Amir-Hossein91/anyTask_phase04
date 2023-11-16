package com.example.phase_04.mapper;

import com.example.phase_04.dto.request.OrderRequestDTO;
import com.example.phase_04.dto.response.ManagerOrderReportDTO;
import com.example.phase_04.dto.response.OrderResponseDTO;
import com.example.phase_04.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "subAssistance.title", source = "subAssistanceTitle")
    @Mapping(target = "orderDescription.customerSuggestedPrice", source = "customerSuggestedPrice")
    @Mapping(target = "orderDescription.customerDesiredDateAndTime", source = "customerDesiredDateAndTime")
    @Mapping(target = "orderDescription.taskDetails", source = "taskDetails")
    @Mapping(target = "orderDescription.address", source = "address")
    @Mapping(target = "subAssistance.assistance.title", source = "assistanceTitle")
    Order dtoToModel (OrderRequestDTO requestDTO);

    @Mapping(target = "subAssistanceTitle", source = "order.subAssistance.title")
    @Mapping(target = "customerId", source = "order.customer.id")
    @Mapping(target = "technicianId", source = "order.technician.id")
    @Mapping(target = "orderRegistrationDateAndTime", source = "order.orderRegistrationDateAndTime")
    @Mapping(target = "customerSuggestedPrice", source = "order.orderDescription.customerSuggestedPrice")
    @Mapping(target = "customerDesiredDateAndTime", source = "order.orderDescription.customerDesiredDateAndTime")
    @Mapping(target = "taskDetails", source = "order.orderDescription.taskDetails")
    @Mapping(target = "address", source = "order.orderDescription.address")
    @Mapping(target = "orderStatus", source = "order.orderStatus")
    OrderResponseDTO modelToDto (Order order);

    @Mapping(target = "subAssistanceTitle", source = "order.subAssistance.title")
    @Mapping(target = "customerId", source = "order.customer.id")
    @Mapping(target = "technicianId", source = "order.technician.id")
    @Mapping(target = "orderRegistrationDateAndTime", source = "order.orderRegistrationDateAndTime")
    @Mapping(target = "customerSuggestedPrice", source = "order.orderDescription.customerSuggestedPrice")
    @Mapping(target = "customerDesiredDateAndTime", source = "order.orderDescription.customerDesiredDateAndTime")
    @Mapping(target = "taskDetails", source = "order.orderDescription.taskDetails")
    @Mapping(target = "address", source = "order.orderDescription.address")
    @Mapping(target = "orderStatus", source = "order.orderStatus")
    @Mapping(target = "acceptedPrice", source = "order.chosenTechnicianSuggestion.techSuggestedPrice")
    @Mapping(target = "acceptedDate", source = "order.chosenTechnicianSuggestion.techSuggestedDate")
    @Mapping(target = "estimatedTaskDuration", source = "order.chosenTechnicianSuggestion.taskEstimatedDuration")
    @Mapping(target = "taskStartDate", source = "order.startedTime")
    @Mapping(target = "taskFinishDate", source = "order.finishedTime")
    ManagerOrderReportDTO modelToReport (Order order);
}
