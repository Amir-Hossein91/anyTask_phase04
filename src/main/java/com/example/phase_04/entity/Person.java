package com.example.phase_04.entity;

import com.example.phase_04.entity.base.BaseEntity;
import com.example.phase_04.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@SequenceGenerator(name = "id_generator", sequenceName = "person_sequence")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "Person_Role",discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("No role")

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Person extends BaseEntity implements UserDetails {
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String username;
    private String password;
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;
    @Enumerated(value = EnumType.STRING)
    private Role role;
    private boolean clickedActivationLink;
    private int orderCount;

    public String toString() {
        return  "\tfirstName = " + this.getFirstName() +
                "\n\tlastName = " + this.getLastName() +
                "\n\t" + super.toString() +
                "\n\temail = " + this.getEmail() +
                "\n\tusername = " + this.getUsername() +
                "\n\tregistrationDate = " + BaseEntity.getPersianDateTime(this.getRegistrationDate());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return clickedActivationLink;
    }
}
