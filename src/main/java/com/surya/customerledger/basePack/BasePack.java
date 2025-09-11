package com.surya.customerledger.basePack;

import com.surya.customerledger.company.Company;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
public class BasePack {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  private String name;

  @NotNull
  private Integer customerPrice;

  @NotNull
  private Integer lcoPrice;

  @UpdateTimestamp
  private Instant updatedAt;

  @ManyToOne
  @JoinColumn(name = "company_id")
  @NotNull
  private Company company;

  public BasePack() {
  }

  public BasePack(String name, Integer customerPrice, Integer lcoPrice, Company company) {
    this.name = name;
    this.customerPrice = customerPrice;
    this.lcoPrice = lcoPrice;
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

  public Integer getCustomerPrice() {
    return customerPrice;
  }

  public void setCustomerPrice(Integer customerPrice) {
    this.customerPrice = customerPrice;
  }

  public Integer getLcoPrice() {
    return lcoPrice;
  }

  public void setLcoPrice(Integer lcoPrice) {
    this.lcoPrice = lcoPrice;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }
}
