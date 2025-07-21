package com.surya.customerledger.connection;

import com.surya.customerledger.area.Area;
import com.surya.customerledger.basePack.BasePack;
import com.surya.customerledger.company.Company;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
public class Connection {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name;

  private String boxNumber;

  private String phoneNumber;

  private Instant lastPayment;

  @CreationTimestamp
  private Instant createdAt;

  @UpdateTimestamp
  private Instant updatedDate;

  @ManyToOne(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "company_id")
  private Company company;

  @ManyToOne
  @JoinColumn(name = "area_id")
  private Area area;

  @ManyToOne
  @JoinColumn(name = "basePack_id")
  private BasePack basePack;

  public Connection() {
  }

  public Connection(String name, String boxNumber, String phoneNumber, Company company, Area area, BasePack basePack) {
    this.name = name;
    this.boxNumber = boxNumber;
    this.phoneNumber = phoneNumber;
    this.lastPayment = lastPayment;
    this.createdAt = createdAt;
    this.updatedDate = updatedDate;
    this.company = company;
    this.area = area;
    this.basePack = basePack;
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

  public String getBoxNumber() {
    return boxNumber;
  }

  public void setBoxNumber(String boxNumber) {
    this.boxNumber = boxNumber;
  }

  public Company getCompany() {
    return company;
  }

  public Area getArea() {
    return area;
  }

  public void setArea(Area area) {
    this.area = area;
  }

  public BasePack getBasePack() {
    return basePack;
  }

  public void setBasePack(BasePack basePack) {
    this.basePack = basePack;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public Instant getLastPayment() {
    return lastPayment;
  }

  public void setLastPayment(Instant lastPayment) {
    this.lastPayment = lastPayment;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getUpdatedDate() {
    return updatedDate;
  }

  public void setUpdatedDate(Instant updatedDate) {
    this.updatedDate = updatedDate;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
}
