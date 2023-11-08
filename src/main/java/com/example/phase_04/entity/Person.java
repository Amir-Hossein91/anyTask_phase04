package com.example.phase_04.entity;

import com.example.phase_04.entity.base.BaseEntity;
import com.example.phase_04.entity.enums.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^[^\\d]{3,}$", message = "first name should be at least three characters and " +
            "no digits are allowed")
    @Column(name = "first_name")
    private String firstName;
    @Pattern(regexp = "^[^\\d]{3,}$", message = "last name should be at least three characters and " +
            "no digits are allowed")
    @Column(name = "last_name")
    private String lastName;
    @Column(unique = true)
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Invalid email address format")
    private String email;
    @Column(unique = true)
    @NotNull(message = "Username can not be null")
    @Pattern(regexp = "^[^\\s]+$", message = "Username can not be empty")
    private String username;
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "Password must be exactly " +
            "8 characters containing digits and letters")
    private String password;
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;
    @Enumerated(value = EnumType.STRING)
    private Roles role;
    private boolean enabled;

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

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
