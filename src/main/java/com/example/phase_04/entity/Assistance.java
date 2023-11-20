package com.example.phase_04.entity;

import com.example.phase_04.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;


@Entity
@SequenceGenerator(name = "id_generator", sequenceName = "assistance_sequence")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Component
public class Assistance extends BaseEntity {
    @Column(unique = true)
    private String title;

    public String toString() {
        return  this.getTitle() ;
    }
}
