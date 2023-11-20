package com.example.phase_04.entity;

import com.example.phase_04.entity.enums.TechnicianStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@DiscriminatorValue("Technician")

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Technician extends Person {
    private long credit;
    @ManyToMany(mappedBy = "technicians")
    private List<SubAssistance> subAssistances;
    private int score;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "technician_Status")
    private TechnicianStatus technicianStatus;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] image;
    @Column(name = "is_Active")
    private boolean isActive;

    public String toString() {
        return super.toString() +
                "\n\tscore = " + this.getScore() +
                "\n\tcredit = " + this.getCredit() +
                "\n\ttechnician_Status = " + this.getTechnicianStatus() +
                "\n\tis_Active = " + this.isActive() +
                "\n\tsub_Assistances = " + this.subAssistances;
    }
}
