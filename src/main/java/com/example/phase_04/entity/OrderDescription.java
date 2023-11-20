package com.example.phase_04.entity;

import com.example.phase_04.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@SequenceGenerator(name = "id_generator", sequenceName = "order_description_sequence")
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderDescription extends BaseEntity {
    @Column(name = "customer_Suggested_Price")
    private long customerSuggestedPrice;
    @Column(name = "customer_Desired_Date_And_Time")
    private LocalDateTime customerDesiredDateAndTime;
    @Column(name = "task_details")
    private String taskDetails;
    private String address;

    public String toString() {
        return "\n\t\t" + super.toString() +
                "\n\t\tcustomer_Suggested_Price = " + this.getCustomerSuggestedPrice() +
                "\n\t\tcustomer_Desired_Date_And_Time = " + BaseEntity.getPersianDateTime(this.getCustomerDesiredDateAndTime()) +
                "\n\t\ttask_Details = " + this.getTaskDetails() +
                "\n\t\taddress = " + this.getAddress();
    }
}
