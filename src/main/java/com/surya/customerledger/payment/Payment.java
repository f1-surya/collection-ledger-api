package com.surya.customerledger.payment;

import com.surya.customerledger.basePack.BasePack;
import com.surya.customerledger.company.Company;
import com.surya.customerledger.connection.Connection;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "connection_id")
  @NotNull
  private Connection connection;

  @CreationTimestamp
  private Instant date;

  @ManyToOne
  @JoinColumn(name = "current_pack_id")
  @NotNull
  private BasePack currentPack;

  @NotNull
  private Boolean isMigration = false;

  @ManyToOne
  @JoinColumn(name = "to_pack_id")
  private BasePack to;

  @NotNull
  private Integer customerPrice;

  @NotNull
  private Integer lcoPrice;

  @ManyToOne
  @JoinColumn(name = "company_id")
  @NotNull
  private Company company;

  public Payment() {
  }

  public Payment(Connection connection,
                 BasePack currentPack,
                 BasePack to,
                 Integer customerPrice,
                 Integer lcoPrice,
                 Company company) {
    this.connection = connection;
    this.currentPack = currentPack;
    this.to = to;
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

  public Connection getConnection() {
    return connection;
  }

  public void setConnection(Connection connection) {
    this.connection = connection;
  }

  public Instant getDate() {
    return date;
  }

  public void setDate(Instant date) {
    this.date = date;
  }

  public BasePack getCurrentPack() {
    return currentPack;
  }

  public void setCurrentPack(BasePack currentPack) {
    this.currentPack = currentPack;
  }

  public BasePack getTo() {
    return to;
  }

  public void setTo(BasePack to) {
    this.to = to;
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

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public Boolean getMigration() {
    return isMigration;
  }

  public void setMigration(Boolean migration) {
    isMigration = migration;
  }
}
