package com.surya.customerledger.company;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CompanyDto(@NotBlank String name, @Email String email) {
}
