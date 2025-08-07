package com.surya.customerledger.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class SignupFormData {

  @NotNull(message = "Name shouldn't be empty")
  @Size(min = 3, message = "Name should be at least 3 characters long.")
  private String name;

  @NotNull(message = "Email is required")
  @Email(message = "Not a valid email")
  private String email;

  @NotNull(message = "Password shouldn't be empty")
  @Size(min = 8, message = "Password should be at least 8 characters long.")
  private String password;

  @NotNull(message = "Repeated password shouldn't be empty")
  @Size(min = 8, message = "Password should be at least 8 characters long.")
  private String passwordRepeat;

  public SignupFormData(String name, String email, String password, String passwordRepeat) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.passwordRepeat = passwordRepeat;
  }

  public SignupFormData() {
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPasswordRepeat() {
    return passwordRepeat;
  }

  public void setPasswordRepeat(String passwordRepeat) {
    this.passwordRepeat = passwordRepeat;
  }

  @Override
  public String toString() {
    return "SignupFormData[" +
        "name=" + name + ", " +
        "email=" + email + ", " +
        "password=" + password + ", " +
        "passwordRepeat=" + passwordRepeat + ']';
  }

}
