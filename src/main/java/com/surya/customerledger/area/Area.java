package com.surya.customerledger.area;

import com.surya.customerledger.company.Company;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Area {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  private String name;

  @ManyToOne(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "company_id")
  @NotNull
  private Company company;

  public Area() {}

  public Area(String name, Company company) {
    this.name = name;
    this.company = company;
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

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }
}
