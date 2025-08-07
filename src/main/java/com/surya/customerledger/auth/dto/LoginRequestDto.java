package com.surya.customerledger.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record LoginRequestDto(
    @NotNull(message = "Email is required") @Email(message = "Invalid email format.") String email,
    @NotNull(message = "Password is required") @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
        message = "Password must be at least 8 characters long and contain at least one digit, uppercase and lowercase letters."
    ) String password) {
}
