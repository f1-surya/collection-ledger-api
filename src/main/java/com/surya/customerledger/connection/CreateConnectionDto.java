package com.surya.customerledger.connection;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateConnectionDto(
    @NotNull(message = "Name is required")
    @Size(min = 3, message = "Name needs to be at-least 3 characters long")
    String name,

    @NotNull(message = "Box number is required")
    @Size(min = 10, message = "Box number should be at-least 10 characters long")
    String boxNumber,

    @Size(min = 10, message = "Phone number should be at-least 10 characters")
    String phoneNumber,

    @NotNull(message = "Area is required")
    Integer area,

    @NotNull(message = "Base pack is required")
    Integer basePack
) {
}
