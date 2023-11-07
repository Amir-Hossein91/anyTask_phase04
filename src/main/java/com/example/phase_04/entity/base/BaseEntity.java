package com.example.phase_04.entity.base;

import com.github.mfathi91.time.PersianDate;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "id_generator")
    private long id;

    public String toString() {
        return "id = " + this.getId();
    }

    public static String getPersianDateTime(LocalDateTime dateTime){
        String[] date = dateTime.toString().split("T");
        return PersianDate.fromGregorian(dateTime.toLocalDate()) + "  " + date[1];
    }
}
