package com.surya.customerledger.company;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CompanyDto(
    @NotNull(message = "Name is required") @NotEmpty(message = "Name shouldn't be blank") String name,
    @NotNull(message = "Email is required") @Email(message = "Not a valid email") String email) {
}
