package com.surya.customerledger.company;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CompanyDto {

  @NotNull(message = "Name is required")
  @Size(message = "Name should have at least 3 letters")
  private String name;

  @NotNull(message = "Email is required")
  @Email(message = "Not a valid email")
  private String email;

  public CompanyDto() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
