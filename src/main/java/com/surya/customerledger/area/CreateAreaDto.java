package com.surya.customerledger.area;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateAreaDto(
    @NotNull(message = "Name is required") @NotEmpty(message = "Name mustn't be empty") String name) {
}
