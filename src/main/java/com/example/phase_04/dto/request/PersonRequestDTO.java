package com.example.phase_04.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PersonRequestDTO(
                               @Pattern(regexp = "^[^\\d]{3,}$", message = "first name should be at least three characters and " +
                                        "no digits are allowed")
                               String firstName,
                               @Pattern(regexp = "^[^\\d]{3,}$", message = "last name should be at least three characters and " +
                                       "no digits are allowed")
                               String lastName,
                               @Email
                               String email,
                               @NotBlank(message = "Username can not be blank")
                               @Pattern(regexp = "^[^\\s]+$", message = "Username can not contain white spaces")
                               String username,
                               @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "Password must be at least " +
                                       "8 characters containing digits and letters")
                               String password) {
}
