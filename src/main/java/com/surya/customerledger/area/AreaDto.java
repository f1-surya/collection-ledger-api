package com.surya.customerledger.area;

public class AreaDto {
  private Integer id;
  private String name;
  private Long connections;

  public AreaDto(Integer id, String name, Long connections) {
    this.id = id;
    this.name = name;
    this.connections = connections;
  }

  public AreaDto() {
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

  public Long getConnections() {
    return connections;
  }

  public void setConnections(Long connections) {
    this.connections = connections;
  }
}
