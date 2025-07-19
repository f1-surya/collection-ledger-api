package com.surya.customerledger.area;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UpdateAreaDto(
    @NotNull(message = "ID is required") Integer id,
    @NotNull(message = "Name is required") @NotEmpty(message = "Name shouldn't be blank") String name) {
}
