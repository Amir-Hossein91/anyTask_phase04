package com.example.phase_04.controller.requestObjects;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class ChangePassword {
    private String username;
    private String currentPassword;
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "Password must be at least " +
            "8 characters containing digits and letters")
    private String newPassword;
}
