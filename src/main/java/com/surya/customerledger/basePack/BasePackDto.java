package com.surya.customerledger.basePack;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record BasePackDto(
    @NotNull(message = "Name shouldn't be empty") @NotEmpty(message = "Name shouldn't be empty") String name,
    @NotNull(message = "LCO price is required") Integer lcoPrice,
    @NotNull(message = "Customer price is required") Integer customerPrice) {
}
