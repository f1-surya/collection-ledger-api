package com.surya.customerledger.company;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CompanyDto(
    @NotBlank(message = "Name is required") String name,
    @NotNull(message = "Email is required") @Email(message = "Not a valid email") String email) {
}
