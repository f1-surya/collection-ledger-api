package com.surya.customerledger.basePack;

import com.surya.customerledger.company.Company;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class BasePack {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name;

  private Integer customerPrice;

  private Integer lcoPrice;

  @Temporal(TemporalType.TIMESTAMP)
  private Date updatedAt;

  @ManyToOne(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "company_id")
  private Company company;

  public BasePack() {
  }

  public BasePack(Integer id, String name, Integer customerPrice, Integer lcoPrice, Date updatedAt, Company company) {
    this.id = id;
    this.name = name;
    this.customerPrice = customerPrice;
    this.lcoPrice = lcoPrice;
    this.updatedAt = updatedAt;
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

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }
}
