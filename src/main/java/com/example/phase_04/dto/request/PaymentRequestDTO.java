package com.example.phase_04.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Range;

public record PaymentRequestDTO(@NotNull(message = "Customer username can not be null")
                                String customerUsername,
                                @NotNull(message = "order ID must be defined")
                                Long orderId,
                                @Pattern(regexp = "^[\\d]{16}$",message = "Card number must have 16 digits")
                                String creditCardNumber,
                                @Range(min = 100, max = 9999, message = "CVV2 must have 3 or 4 digits")
                                int cvv2,
                                @Pattern(regexp = "^[\\d]{5,8}$",message = "Second password must have 5 to 8 digits")
                                String secondPassword,
                                String captchaValue) {
}
