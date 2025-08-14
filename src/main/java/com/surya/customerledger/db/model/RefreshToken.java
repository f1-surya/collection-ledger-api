package com.surya.customerledger.db.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class RefreshToken {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer Id;
  private Integer userId;
  private Instant expiresAt;
  private Instant createdAt = Instant.now();
  private String token;

  public RefreshToken(Integer userId, String token, Instant expiresAt) {
    this.userId = userId;
    this.expiresAt = expiresAt;
    this.token = token;
  }

  public RefreshToken() {

  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(Instant expiresAt) {
    this.expiresAt = expiresAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }
}
