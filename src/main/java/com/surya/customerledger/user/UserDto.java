package com.surya.customerledger.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserDto {
  @NotNull(message = "ID is required")
  private Integer id;

  @NotNull(message = "Name is required")
  @Size(min = 4, message = "Name must be at least 4 characters long.")
  private String name;

  @NotNull(message = "Email is required")
  @Email(message = "Invalid email")
  private String email;

  private String role;

  public UserDto() {
  }

  public UserDto(Integer id, String name, String email, String role) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.role = role;
  }

  public static UserDto fromUser(User user) {
    return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getRole());
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }
}
