package com.surya.customerledger.company;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CompanyDto {

  @NotNull(message = "Name is required")
  @Size(message = "Name should have at least 3 letters")
  private String name;

  @NotNull(message = "Email is required")
  @Email(message = "Not a valid email")
  private String email;

  @NotNull(message = "Phone number is required.")
  @Pattern(regexp = "^\\+?(\\d{1,3})?[-.\\s]?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$", message = "Please enter a valid phone number.")
  private String phone;

  @NotNull
  @Size(min = 10, message = "Address should be at least 10 characters long.")
  private String address;

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

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
