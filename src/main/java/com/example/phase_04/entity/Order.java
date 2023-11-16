package com.example.phase_04.entity;

import com.example.phase_04.entity.base.BaseEntity;
import com.example.phase_04.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@SequenceGenerator(name = "id_generator", sequenceName = "order_sequence")
@Table(name = "orders")
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {
    @ManyToOne
    private SubAssistance subAssistance;
    @ManyToOne
    private Customer customer;
    @OneToOne
    private Technician technician;
    @Column(name = "order_Registration_Date_And_Time")
    private LocalDateTime orderRegistrationDateAndTime;
    @OneToOne
    @Cascade(value = org.hibernate.annotations.CascadeType.PERSIST)
    private OrderDescription orderDescription;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;
    @Range(min = 1, max = 5, message = "Technician score should be between 1 and 5")
    @Column(name = "technician_score")
    private int technicianScore;
    @OneToMany(mappedBy = "order",fetch = FetchType.EAGER)
    @Cascade(value = org.hibernate.annotations.CascadeType.MERGE)
    private List<TechnicianSuggestion> technicianSuggestions;
    @Column(name = "technician_evaluation")
    private String techEvaluation;
    @OneToOne
    private TechnicianSuggestion chosenTechnicianSuggestion;
    private LocalDateTime startedTime;
    private LocalDateTime finishedTime;
    private boolean isTechnicianScored;


    public String toString() {
        return super.toString() +
                "\n\tsub_Assistance = " + this.getSubAssistance().getTitle() +
                "\n\tcustomer = " + this.getCustomer().getId() +
                "\n\ttechnician = " + (this.getTechnician()==null ? "[]": this.getTechnician().getId()) +
                "\n\torder_Description = " + this.getOrderDescription() +
                "\n\torder_Status = " + this.getOrderStatus() +
                "\n\ttechnician_Score = " + this.getTechnicianScore() +
                "\n\ttechnician_Suggestions = " + this.getTechnicianSuggestions() +
                "\n\ttechnician_Evaluation = " + this.getTechEvaluation() ;
    }
}
