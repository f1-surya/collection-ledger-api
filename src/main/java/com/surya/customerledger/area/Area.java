package com.surya.customerledger.area;

import com.surya.customerledger.company.Company;
import jakarta.persistence.*;

@Entity
public class Area {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name;

  @ManyToOne(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "company_id")
  private Company company;

  public Area() {}

  public Area(Integer id, String name, Company company) {
    this.id = id;
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
