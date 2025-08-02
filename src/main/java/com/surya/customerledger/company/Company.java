package com.surya.customerledger.company;

import com.surya.customerledger.db.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Company {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  private String name;

  @NotNull
  private String email;

  @OneToOne
  @JoinColumn(name = "owner_id")
  @NotNull
  private User owner;

  public Company() {
  }

  public Company(String name, String email, User owner) {
    this.name = name;
    this.email = email;
    this.owner = owner;
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

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
