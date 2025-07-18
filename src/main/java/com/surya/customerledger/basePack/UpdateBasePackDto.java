package com.surya.customerledger.basePack;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UpdateBasePackDto(
    @NotNull(message = "Id is required") Integer id,
    @NotNull(message = "Name is required") @NotEmpty(message = "Name shouldn't be empty") String name,
    @NotNull(message = "Customer price is required") Integer customerPrice,
    @NotNull(message = "LCO price is required") Integer lcoPrice) {
}
