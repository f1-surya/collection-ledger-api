package com.surya.customerledger.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class HashEncoder {
  private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

  public String encode(String rawPassword) {
    return bcrypt.encode(rawPassword);
  }

  public boolean matches(String rawPassword, String hash) {
    return bcrypt.matches(rawPassword, hash);
  }
}
