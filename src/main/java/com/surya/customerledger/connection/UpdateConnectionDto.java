package com.surya.customerledger.connection;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateConnectionDto(
    @NotNull(message = "ID is required") Integer id,
    @NotNull(message = "Name is required") @NotEmpty(message = "Name shouldn't be blank") String name,
    @NotNull(message = "Area is required") Integer area,
    @Size(min = 10, message = "Phone number should be at-least 10 characters") String phoneNumber,
    @NotNull(message = "Box number is required") @Size(min = 10, message = "Phone number should be at-least 10 characters") String boxNumber
) {
}
