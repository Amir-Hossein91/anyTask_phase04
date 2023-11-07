package com.example.phase_04.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Range;

@Entity
@DiscriminatorValue("Customer")

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends Person {
    @Range(min = 0, message = "Credit can not be negative")
    private long credit;

    public String toString() {
        return super.toString() +
                "\n\tcredit=" + this.getCredit();
    }
}
