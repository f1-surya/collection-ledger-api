package com.surya.customerledger.basePack;

public class BasePackConCount {
  private Integer id;
  private String name;
  private Integer lcoPrice;
  private Integer customerPrice;
  private Long connections;

  public BasePackConCount(Integer id, String name, Integer lcoPrice, Integer customerPrice, Long connections) {
    this.id = id;
    this.name = name;
    this.lcoPrice = lcoPrice;
    this.customerPrice = customerPrice;
    this.connections = connections;
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

  public Integer getLcoPrice() {
    return lcoPrice;
  }

  public void setLcoPrice(Integer lcoPrice) {
    this.lcoPrice = lcoPrice;
  }

  public Integer getCustomerPrice() {
    return customerPrice;
  }

  public void setCustomerPrice(Integer customerPrice) {
    this.customerPrice = customerPrice;
  }

  public Long getConnections() {
    return connections;
  }

  public void setConnections(Long connections) {
    this.connections = connections;
  }
}
